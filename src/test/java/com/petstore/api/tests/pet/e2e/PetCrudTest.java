package com.petstore.api.tests.pet.e2e;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.flow.PetFlow;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.link;
import static io.qameta.allure.Allure.step;
import static com.petstore.api.assertions.PetAssertions.assertThat;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Tag("e2e")
public class PetCrudTest extends BaseApiTest {

    private final PetFlow petFlow = new PetFlow();
    private final PetClient petClient = new PetClient();

    @Test
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Deve executar CRUD completo do Pet com contrato e SLA")
    void deveExecutarCrudCompletoDoPet() {
        Pet newPet = PetDataFactory.newPet();
        long petId = newPet.getId();

        link("Pet Schema", "https://github.com/bruno-salzani/petstore-api-restassured-java/blob/main/src/test/resources/schemas/pet.json");
        addAttachment("Pet Schema", "application/json", readResource("schemas/pet.json"));

        step("Cadastrar um novo pet e verificar", () -> {
            petFlow.addPetAndVerify(newPet);
        });

        step("Atualizar o status do pet para sold", () -> {
            Pet updatedPet = PetDataFactory.updatedPet(newPet);
            petClient.updatePet(updatedPet)
                    .then()
                    .statusCode(200)
                    .body("status", org.hamcrest.Matchers.is("sold"));
            Pet createdPet = petClient.getPet(petId).as(Pet.class);
            org.hamcrest.MatcherAssert.assertThat(createdPet.getStatus(), org.hamcrest.Matchers.is("sold"));
            assertThat(createdPet).hasName(newPet.getName());
            org.hamcrest.MatcherAssert.assertThat(createdPet.getId(), org.hamcrest.Matchers.is(petId));
            org.hamcrest.MatcherAssert.assertThat(createdPet.getCategory().getId(), org.hamcrest.Matchers.is(newPet.getCategory().getId()));
            org.hamcrest.MatcherAssert.assertThat(createdPet.getCategory().getName(), org.hamcrest.Matchers.is(newPet.getCategory().getName()));
            org.hamcrest.MatcherAssert.assertThat(createdPet.getPhotoUrls(), org.hamcrest.Matchers.hasItem(newPet.getPhotoUrls().get(0)));
        });

        step("Validar contrato do GET do pet", () -> {
            petClient.getPet(petId)
                    .then()
                    .body(matchesJsonSchemaInClasspath("schemas/pet.json"));
        });

        step("Deletar o pet", () -> {
            Response response = petClient.deletePet(petId);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200);
        });

        step("Validar que o pet foi removido", () -> {
            Response response = petClient.getPet(petId);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(404);
        });
    }

    @ParameterizedTest
    @Tag("regression")
    @ValueSource(strings = {"available", "pending", "sold"})
    @DisplayName("Deve atualizar status do Pet para variações válidas")
    void deveAtualizarStatusVariacoes(String novoStatus) {
        Pet newPet = PetDataFactory.newPet();
        long petId = newPet.getId();

        step("Criar pet", () -> {
            petClient.createPet(newPet).then().statusCode(200);
        });

        step("Atualizar status para " + novoStatus, () -> {
            Pet updated = com.petstore.api.data.model.Pet.builder()
                    .id(newPet.getId())
                    .name(newPet.getName())
                    .category(newPet.getCategory())
                    .photoUrls(newPet.getPhotoUrls())
                    .tags(newPet.getTags())
                    .status(novoStatus)
                    .build();
            petClient.updatePet(updated).then().statusCode(200);
            Pet fetched = petClient.getPet(petId).as(Pet.class);
            org.hamcrest.MatcherAssert.assertThat(fetched.getStatus(), org.hamcrest.Matchers.is(novoStatus));
        });
    }

    @Test
    @Tag("regression")
    @DisplayName("Deve retornar 404 ao buscar pet inexistente")
    void deveRetornar404AoBuscarPetInexistente() {
        long petId = PetDataFactory.nonExistentPetId();

        step("Buscar pet inexistente", () -> {
            Response response = petClient.getPet(petId);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(404);
        });
    }

    private String readResource(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return "";
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }
}

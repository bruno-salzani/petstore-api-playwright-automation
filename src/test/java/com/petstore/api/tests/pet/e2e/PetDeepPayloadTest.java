package com.petstore.api.tests.pet.e2e;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

public class PetDeepPayloadTest extends BaseApiTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @DisplayName("Deve validar payload profundo no caminho alternativo")
    void deveValidarPayloadProfundo() {
        Pet pet = PetDataFactory.newPet();

        step("Criar pet", () -> {
            Response response = petClient.createPet(pet);
            response.then().statusCode(200);
        });

        step("Buscar e validar campos detalhados", () -> {
            Pet fetched = petClient.getPet(pet.getId()).as(Pet.class);
            org.hamcrest.MatcherAssert.assertThat(fetched.getId(), Matchers.is(pet.getId()));
            org.hamcrest.MatcherAssert.assertThat(fetched.getName(), Matchers.is(pet.getName()));
            org.hamcrest.MatcherAssert.assertThat(fetched.getCategory(), Matchers.notNullValue());
            org.hamcrest.MatcherAssert.assertThat(fetched.getCategory().getName(), Matchers.is(pet.getCategory().getName()));
            org.hamcrest.MatcherAssert.assertThat(fetched.getTags(), Matchers.notNullValue());
            org.hamcrest.MatcherAssert.assertThat(fetched.getTags().stream().anyMatch(t -> t.getName() != null && t.getName().startsWith("automation-")), Matchers.is(true));
        });
    }
}

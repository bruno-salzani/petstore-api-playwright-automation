package com.petstore.api.tests.pet.consistency;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

import static org.hamcrest.Matchers.is;

public class PetConsistencyTest extends BaseApiTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @DisplayName("Deve manter consistência em POST duplicado")
    void deveManterConsistenciaEmPostDuplicado() {
        Pet newPet = PetDataFactory.newPet();

        step("Criar pet pela primeira vez", () -> {
            Response response = petClient.createPet(newPet);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    
                    .body("id", is(newPet.getId().intValue()));
        });

        step("Criar pet com o mesmo id novamente", () -> {
            Response response = petClient.createPet(newPet);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    
                    .body("id", is(newPet.getId().intValue()));
        });
    }

    @Test
    @Tag("regression")
    @DisplayName("Deve manter consistência ao deletar duas vezes")
    void deveManterConsistenciaAoDeletarDuasVezes() {
        Pet newPet = PetDataFactory.newPet();

        step("Criar pet para exclusão", () -> {
            Response response = petClient.createPet(newPet);
            response.then().statusCode(200);
        });

        step("Deletar pet", () -> {
            Response response = petClient.deletePet(newPet.getId());
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    ;
        });

        step("Deletar pet novamente", () -> {
            Response response = petClient.deletePet(newPet.getId());
            response.then()
                    .log().ifValidationFails()
                    .statusCode(404);

            String body = response.getBody().asString();
            if (body != null && !body.isBlank()) {
                
            }
        });
    }

    @Test
    @Tag("regression")
    @DisplayName("Deve refletir alteração no GET após PUT")
    void deveRefletirAlteracaoNoGetAposPut() {
        Pet newPet = PetDataFactory.newPet();
        Pet updatedPet = PetDataFactory.updatedPet(newPet);

        step("Criar pet", () -> {
            Response response = petClient.createPet(newPet);
            response.then().statusCode(200);
        });

        step("Atualizar status do pet", () -> {
            Response response = petClient.updatePet(updatedPet);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("status", is("sold"));
        });

        step("Buscar pet atualizado", () -> {
            Response response = petClient.getPet(newPet.getId());
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body("status", is("sold"));
        });
    }
}

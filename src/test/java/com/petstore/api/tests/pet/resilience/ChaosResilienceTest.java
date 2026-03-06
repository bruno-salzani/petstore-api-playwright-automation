package com.petstore.api.tests.pet.resilience;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

public class ChaosResilienceTest extends BaseApiTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @DisplayName("Deve passar com caos habilitado ao criar pet (retry eficaz)")
    void devePassarComCaosHabilitadoAoCriarPet() {
        Pet newPet = PetDataFactory.newPet();
        System.setProperty("enable.chaos", "true");

        step("Criar pet com caos habilitado", () -> {
            Response response = petClient.createPet(newPet);
            response.then().statusCode(200);
        });

        System.clearProperty("enable.chaos");
    }
}

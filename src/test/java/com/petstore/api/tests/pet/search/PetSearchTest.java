package com.petstore.api.tests.pet.search;

import com.petstore.api.client.PetClient;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

public class PetSearchTest extends BaseApiTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @DisplayName("Deve retornar pets por status com contrato básico")
    void deveRetornarPetsPorStatus() {
        step("Buscar pets com status available", () -> {
            Response response = petClient.findByStatus("available");
            response.then()
                    .statusCode(200)
                    .header("Content-Type", Matchers.containsStringIgnoringCase("application/json"));
        });
    }
}

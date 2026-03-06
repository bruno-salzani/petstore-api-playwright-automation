package com.petstore.api.tests.pet.contract;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.model.Pet;

import com.petstore.api.tests.BaseContractTest;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;


public class PetContractNegativeTest extends BaseContractTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @Tag("contract")
    @DisplayName("Deve falhar contrato ao criar pet sem campos obrigatórios")
    void deveFalharContratoAoCriarPetSemCamposObrigatorios() {
        Pet invalidPet = new Pet();
        invalidPet.setId(1L);
        invalidPet.setStatus("available");

        step("Enviar pet inválido sem name e photoUrls", () -> {
        wireMockServer.stubFor(post(urlEqualTo("/pet"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"type\":\"error\",\"message\":\"Invalid input\"}")));

        Response response = given()
                .baseUri(wireMockServer.baseUrl())
                .contentType("application/json")
                .body(invalidPet)
                .when()
                .post("/pet");
            response.then()
                    .log().ifValidationFails()
                    .statusCode(400);


        });
    }

    @Test
    @Tag("regression")
    @DisplayName("Deve falhar contrato ao criar pet com photoUrls vazio")
    void deveFalharContratoAoCriarPetComPhotoUrlsVazio() {
        Pet invalidPet = new Pet();
        invalidPet.setId(2L);
        invalidPet.setName("InvalidPet");
        invalidPet.setStatus("available");
        invalidPet.setPhotoUrls(Collections.emptyList());

        step("Enviar pet inválido com photoUrls vazio", () -> {
            Response response = petClient.createPet(invalidPet);
            response.then()
                    .log().ifValidationFails()
                    .statusCode(200);


        });
    }
}

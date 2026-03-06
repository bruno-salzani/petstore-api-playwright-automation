package com.petstore.api.tests.pet.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseContractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PetStrictContractTest extends BaseContractTest {

    @Test
    @Tag("contract")
    @Tag("regression")
    @DisplayName("Resposta de criação deve seguir pet-strict.json")
    void respostaCriacaoDeveSeguirPetStrict() throws Exception {
        Pet pet = PetDataFactory.minimalValidPet();
        String json = new ObjectMapper().writeValueAsString(pet);
        wireMockServer.stubFor(post(urlEqualTo("/pet"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));

        step("Validar resposta contra schema estrito", () -> {
            given()
                    .baseUri(wireMockServer.baseUrl())
                    .contentType("application/json")
                    .body(pet)
                    .when()
                    .post("/pet")
                    .then()
                    .statusCode(200)
                    .body(matchesJsonSchemaInClasspath("schemas/pet-strict.json"));
        });
    }

    @Test
    @Tag("contract")
    @DisplayName("Resposta inválida deve falhar contra pet-strict.json")
    void respostaInvalidaDeveFalharContraPetStrict() {
        String invalid = "{\"id\":12345,\"photoUrls\":[\"https://example.com/p.jpg\"],\"status\":\"available\"}";
        wireMockServer.stubFor(post(urlEqualTo("/pet"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(invalid)));

        assertThrows(AssertionError.class, () ->
                given()
                        .baseUri(wireMockServer.baseUrl())
                        .contentType("application/json")
                        .body(invalid)
                        .when()
                        .post("/pet")
                        .then()
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath("schemas/pet-strict.json")));
    }
}

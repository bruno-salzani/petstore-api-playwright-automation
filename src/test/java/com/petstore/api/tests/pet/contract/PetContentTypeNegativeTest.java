package com.petstore.api.tests.pet.contract;

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

public class PetContentTypeNegativeTest extends BaseContractTest {

    @Test
    @Tag("contract")
    @DisplayName("Deve retornar 415 para Content-Type inválido com erro estruturado")
    void deveRetornar415ParaContentTypeInvalido() {
        wireMockServer.stubFor(post(urlEqualTo("/pet"))
                .withHeader("Content-Type", com.github.tomakehurst.wiremock.client.WireMock.matching("text/plain.*"))
                .willReturn(aResponse()
                        .withStatus(415)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":415,\"type\":\"error\",\"message\":\"Unsupported Media Type\"}")));

        step("Enviar POST com text/plain e validar 415 + schema de erro", () -> {
            given()
                    .baseUri(wireMockServer.baseUrl())
                    .contentType("text/plain")
                    .body("invalid")
                    .when()
                    .post("/pet")
                    .then()
                    .statusCode(415)
                    .body(matchesJsonSchemaInClasspath("schemas/error.json"));
        });
    }
}

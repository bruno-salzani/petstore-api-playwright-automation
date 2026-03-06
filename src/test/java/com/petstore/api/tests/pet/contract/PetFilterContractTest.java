package com.petstore.api.tests.pet.contract;

import com.petstore.api.tests.BaseContractTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class PetFilterContractTest extends BaseContractTest {

    @Test
    @Tag("contract")
    @DisplayName("Deve retornar lista para filtro por tags")
    void deveRetornarListaParaFiltroPorTags() {
        wireMockServer.stubFor(get(urlPathEqualTo("/pet/findByTags"))
                .withQueryParam("tags", com.github.tomakehurst.wiremock.client.WireMock.equalTo("foo,bar"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        step("Chamar /pet/findByTags com tags múltiplas", () -> {
            given()
                    .baseUri(wireMockServer.baseUrl())
                    .param("tags", "foo,bar")
                    .when()
                    .get("/pet/findByTags")
                    .then()
                    .statusCode(200)
                    .header("Content-Type", Matchers.containsStringIgnoringCase("application/json"));
        });
    }
}

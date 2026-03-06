package com.petstore.api.tests.pet.contract;

import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseContractTest;
import com.petstore.api.utils.tracing.TracingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class PetTracingContractTest extends BaseContractTest {

    @Test
    @Tag("contract")
    @Tag("regression")
    @DisplayName("Deve enviar traceparent no request e receber 200")
    void deveEnviarTraceparentNoRequest() {
        wireMockServer.stubFor(post(urlEqualTo("/pet"))
                .withHeader("traceparent", matching("00-[0-9a-fA-F]{32}-[0-9a-fA-F]{16}-01"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":200}")));

        Pet pet = PetDataFactory.minimalValidPet();
        step("Enviar POST /pet com traceparent", () -> {
            Response response = given()
                    .baseUri(wireMockServer.baseUrl())
                    .filter(new TracingFilter())
                    .contentType("application/json")
                    .body(pet)
                    .when()
                    .post("/pet");
            response.then().statusCode(200);
        });

        wireMockServer.verify(postRequestedFor(urlEqualTo("/pet"))
                .withHeader("traceparent", matching("00-[0-9a-fA-F]{32}-[0-9a-fA-F]{16}-01")));
    }
}

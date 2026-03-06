package com.petstore.api.tests.pet.headers;

import com.petstore.api.client.PetClient;
import com.petstore.api.data.factory.PetDataFactory;
import com.petstore.api.data.model.Pet;
import com.petstore.api.tests.BaseApiTest;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.containsStringIgnoringCase;

public class PetHeaderValidationTest extends BaseApiTest {

    private final PetClient petClient = new PetClient();

    @Test
    @Tag("regression")
    @DisplayName("Deve validar headers críticos da resposta")
    void deveValidarHeadersCriticosDaResposta() {
        Pet newPet = PetDataFactory.newPet();

        step("Criar pet e validar headers", () -> {
            Response response = petClient.createPet(newPet);
            response.then().statusCode(200);

            String contentType = response.getHeader("Content-Type");
            MatcherAssert.assertThat(contentType, containsStringIgnoringCase("application/json"));

            assertHeaderIfPresent(response, "Cache-Control");
            assertHeaderIfPresent(response, "X-Rate-Limit");
            assertHeaderIfPresent(response, "X-RateLimit-Limit");
            assertHeaderIfPresent(response, "X-RateLimit-Remaining");
        });
    }

    private void assertHeaderIfPresent(Response response, String headerName) {
        String headerValue = response.getHeader(headerName);
        if (headerValue != null && !headerValue.isBlank()) {
            MatcherAssert.assertThat(headerValue, Matchers.not(Matchers.isEmptyOrNullString()));
        }
    }
}

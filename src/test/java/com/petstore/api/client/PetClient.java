package com.petstore.api.client;

import com.petstore.api.config.TestConfig;
import com.petstore.api.core.HttpRetry;
import com.petstore.api.data.model.Pet;
import com.petstore.api.http.Endpoint;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PetClient {

    public Response createPet(Pet pet) {
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .body(pet)
                        .when()
                        .post(Endpoint.PET.path())
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }

    public Response getPet(long petId) {
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .when()
                        .get(Endpoint.PET_BY_ID.path(), petId)
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }

    public Response updatePet(Pet pet) {
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .body(pet)
                        .when()
                        .put(Endpoint.PET.path())
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }

    public Response deletePet(long petId) {
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .when()
                        .delete(Endpoint.PET_BY_ID.path(), petId)
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }

    public Response findByStatus(String status) {
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .param("status", status)
                        .when()
                        .get(Endpoint.FIND_BY_STATUS.path())
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }

    public Response findByTags(String... tags) {
        String joined = String.join(",", tags);
        return HttpRetry.executeWithRetry(() -> given()
                        .spec(TestConfig.requestSpec)
                        .param("tags", joined)
                        .when()
                        .get(Endpoint.FIND_BY_TAGS.path())
                        .then()
                        .spec(TestConfig.responseSpec)
                        .extract()
                        .response(),
                r -> r.getStatusCode() >= 500,
                TestConfig.RETRY_COUNT,
                TestConfig.RETRY_DELAY_MS);
    }
}

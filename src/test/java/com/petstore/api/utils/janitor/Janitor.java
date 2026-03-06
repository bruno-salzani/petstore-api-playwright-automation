package com.petstore.api.utils.janitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.api.client.PetClient;
import com.petstore.api.data.model.Pet;
import com.petstore.api.http.Endpoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public final class Janitor {

    private Janitor() {
    }

    public static void sweep() {
        try {
            long now = Instant.now().getEpochSecond();
            long threshold = now - 24 * 60 * 60; // 24h
            List<Pet> candidates = new ArrayList<>();
            candidates.addAll(fetchByStatus("available"));
            candidates.addAll(fetchByStatus("pending"));
            candidates.addAll(fetchByStatus("sold"));

            PetClient client = new PetClient();
            for (Pet p : candidates) {
                if (p.getTags() == null) continue;
                boolean deletable = p.getTags().stream()
                        .filter(t -> t.getName() != null && t.getName().startsWith("automation-"))
                        .map(t -> t.getName().substring("automation-".length()))
                        .map(Janitor::parseLongSafe)
                        .anyMatch(ts -> ts > 0 && ts < threshold);
                if (deletable) {
                    try {
                        client.deletePet(p.getId());
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static List<Pet> fetchByStatus(String status) {
        try {
            RequestSpecification spec = new RequestSpecBuilder()
                    .setBaseUri(RestAssured.baseURI)
                    .setConfig(RestAssured.config)
                    .setContentType(ContentType.JSON)
                    .build();
            String body = given()
                    .spec(spec)
                    .param("status", status)
                    .when()
                    .get(Endpoint.FIND_BY_STATUS.path())
                    .then()
                    .extract()
                    .asString();
            return new ObjectMapper().readValue(body, new TypeReference<List<Pet>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private static long parseLongSafe(String v) {
        try {
            return Long.parseLong(v);
        } catch (Exception e) {
            return -1L;
        }
    }
}

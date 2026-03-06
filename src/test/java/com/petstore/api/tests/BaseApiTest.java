package com.petstore.api.tests;

import com.petstore.api.config.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseApiTest {

    protected static GenericContainer<?> petstore;

    protected static List<Long> createdPetIds = new ArrayList<>();

    @BeforeAll
    void globalSetup() {
        boolean started = false;
        try {
            petstore = new GenericContainer<>(DockerImageName.parse("swaggerapi/petstore3:latest")).withExposedPorts(8080);
            petstore.start();
            TestConfig.setup(petstore.getMappedPort(8080));
            started = true;
        } catch (Exception ignored) {
        }
        if (!started) {
            String baseUrl = System.getProperty("petstore.baseUrl");
            if (baseUrl == null || baseUrl.isBlank()) {
                baseUrl = "https://petstore.swagger.io/v2";
            }
            TestConfig.setup(baseUrl);
            try {
                com.petstore.api.utils.janitor.Janitor.sweep();
            } catch (Exception ignored) {
            }
        }
        copyAllureCategories();
    }

    @AfterEach
    void cleanupData() {
        var client = new com.petstore.api.client.PetClient();
        for (Long id : createdPetIds) {
            try {
                client.deletePet(id);
            } catch (Exception ignored) {
            }
        }
        createdPetIds.clear();
    }

    @AfterAll
    void teardown() {
        if (petstore != null) {
            try {
                petstore.stop();
            } catch (Exception ignored) {
            }
        }
    }

    private void copyAllureCategories() {
        String resultsDir = System.getProperty("allure.results.directory", "target/allure-results");
        Path targetPath = Path.of(resultsDir, "categories.json");
        Path envPath = Path.of(resultsDir, "environment.properties");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("allure-categories.json")) {
            if (inputStream == null) {
                Files.createDirectories(targetPath.getParent());
            } else {
                Files.createDirectories(targetPath.getParent());
                Files.write(targetPath, inputStream.readAllBytes());
            }
            String env = "Owner=Bruno Salzani" + System.lineSeparator() +
                    "Repository=https://github.com/bruno-salzani/petstore-api-restassured-java" + System.lineSeparator();
            Files.writeString(envPath, env);
        } catch (IOException ignored) {
        }
    }
}

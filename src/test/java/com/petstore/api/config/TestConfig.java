package com.petstore.api.config;

import com.petstore.api.utils.tracing.TracingFilter;
import com.petstore.api.utils.chaos.ChaosFilter;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.lessThan;

public final class TestConfig {
    public static String BASE_URL;
    public static long SLA_MS;
    public static int RETRY_COUNT;
    public static long RETRY_DELAY_MS;
    public static int CONNECT_TIMEOUT_MS;
    public static int SOCKET_TIMEOUT_MS;

    public static RequestSpecification requestSpec;
    public static ResponseSpecification responseSpec;

    private TestConfig() {
    }

    public static void setup(int port) {
        Properties properties = new Properties();
        try (InputStream inputStream = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
        }

        BASE_URL = "http://localhost";

        SLA_MS = Long.parseLong(firstNonNull(
                System.getProperty("petstore.slaMs"),
                System.getenv("PETSTORE_SLA_MS"),
                properties.getProperty("petstore.slaMs"),
                "2000"
        ));
        RETRY_COUNT = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.retryCount"),
                System.getenv("PETSTORE_RETRY_COUNT"),
                properties.getProperty("petstore.retryCount"),
                "2"
        ));
        RETRY_DELAY_MS = Long.parseLong(firstNonNull(
                System.getProperty("petstore.retryDelayMs"),
                System.getenv("PETSTORE_RETRY_DELAY_MS"),
                properties.getProperty("petstore.retryDelayMs"),
                "250"
        ));
        CONNECT_TIMEOUT_MS = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.connectTimeoutMs"),
                System.getenv("PETSTORE_CONNECT_TIMEOUT_MS"),
                "2000"
        ));
        SOCKET_TIMEOUT_MS = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.socketTimeoutMs"),
                System.getenv("PETSTORE_SOCKET_TIMEOUT_MS"),
                "4000"
        ));

        RestAssured.baseURI = BASE_URL;
        RestAssured.port = port;
        RestAssured.basePath = "/api/v3";
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .blacklistHeader("Authorization")
                        .blacklistHeader("api_key"))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECT_TIMEOUT_MS)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT_MS)
                        .setParam("http.connection-manager.timeout", (long) SOCKET_TIMEOUT_MS));
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setConfig(RestAssured.config)
                .addFilter(new AllureRestAssured())
                .addFilter(new TracingFilter())
                .addFilters(enableChaos() ? java.util.List.of(new ChaosFilter()) : java.util.List.of())
                .build();
        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(SLA_MS), TimeUnit.MILLISECONDS)
                .build();
    }

    public static void setup(String baseUrl) {
        Properties properties = new Properties();
        try (InputStream inputStream = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
        }

        BASE_URL = baseUrl;

        SLA_MS = Long.parseLong(firstNonNull(
                System.getProperty("petstore.slaMs"),
                System.getenv("PETSTORE_SLA_MS"),
                properties.getProperty("petstore.slaMs"),
                "2000"
        ));
        RETRY_COUNT = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.retryCount"),
                System.getenv("PETSTORE_RETRY_COUNT"),
                properties.getProperty("petstore.retryCount"),
                "2"
        ));
        RETRY_DELAY_MS = Long.parseLong(firstNonNull(
                System.getProperty("petstore.retryDelayMs"),
                System.getenv("PETSTORE_RETRY_DELAY_MS"),
                properties.getProperty("petstore.retryDelayMs"),
                "250"
        ));
        CONNECT_TIMEOUT_MS = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.connectTimeoutMs"),
                System.getenv("PETSTORE_CONNECT_TIMEOUT_MS"),
                "2000"
        ));
        SOCKET_TIMEOUT_MS = Integer.parseInt(firstNonNull(
                System.getProperty("petstore.socketTimeoutMs"),
                System.getenv("PETSTORE_SOCKET_TIMEOUT_MS"),
                "4000"
        ));

        RestAssured.baseURI = BASE_URL;
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails()
                        .blacklistHeader("Authorization")
                        .blacklistHeader("api_key"))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECT_TIMEOUT_MS)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT_MS)
                        .setParam("http.connection-manager.timeout", (long) SOCKET_TIMEOUT_MS));
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setConfig(RestAssured.config)
                .addFilter(new AllureRestAssured())
                .addFilter(new TracingFilter())
                .addFilters(enableChaos() ? java.util.List.of(new ChaosFilter()) : java.util.List.of())
                .build();
        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(SLA_MS), TimeUnit.MILLISECONDS)
                .build();
    }

    private static boolean enableChaos() {
        String prop = System.getProperty("enable.chaos");
        String env = System.getenv("ENABLE_CHAOS");
        return "true".equalsIgnoreCase(prop) || "true".equalsIgnoreCase(env);
    }

    private static String firstNonNull(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}

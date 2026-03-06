package com.petstore.api.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class BaseContractTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }
}

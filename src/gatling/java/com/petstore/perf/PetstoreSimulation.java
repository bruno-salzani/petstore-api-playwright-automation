package com.petstore.perf;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PetstoreSimulation extends Simulation {

    {
        String base = firstNonBlank(System.getProperty("petstore.baseUrl"), System.getenv("PETSTORE_BASE_URL"), "https://petstore.swagger.io/v2");
        int rps = firstInt(System.getProperty("perf.rps"), System.getenv("PERF_RPS"), 5);
        int durationSec = firstInt(System.getProperty("perf.durationSec"), System.getenv("PERF_DURATION_SEC"), 30);
        HttpProtocolBuilder httpProtocol = http.baseUrl(base)
                .acceptHeader("application/json")
                .contentTypeHeader("application/json");

        ScenarioBuilder scn = scenario("Find Pets by Status")
                .exec(
                        http("findByStatus-available")
                                .get("/pet/findByStatus")
                                .queryParam("status", "available")
                                .check(status().is(200))
                );

        setUp(
                scn.injectOpen(
                        constantUsersPerSec(rps).during(java.time.Duration.ofSeconds(durationSec))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile(95.0).lt(500),
                        global().successfulRequests().percent().gt(99.0)
                );
    }

    private static String firstNonBlank(String... v) {
        for (String s : v) {
            if (s != null && !s.isBlank()) return s;
        }
        return null;
    }

    private static int firstInt(String... valuesAndDefault) {
        for (int i = 0; i < valuesAndDefault.length; i++) {
            String v = valuesAndDefault[i];
            if (i == valuesAndDefault.length - 1) {
                try {
                    return Integer.parseInt(v);
                } catch (Exception ignored) {
                    return 5;
                }
            }
            if (v != null && !v.isBlank()) {
                try {
                    return Integer.parseInt(v);
                } catch (Exception ignored) {
                }
            }
        }
        return 5;
    }
}

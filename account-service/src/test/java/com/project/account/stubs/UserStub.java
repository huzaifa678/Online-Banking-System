package com.project.account.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class UserStub {

    public static void userStubCall(String email) {
        if (email.equals("him25@example.com") ||
            email.equals("user@example.com") || 
            email.equals("123@example.com") ||
            email.equals("example9009@example.com") ||
            email.equals("user123@example.com")) {
            stubFor(get(urlPathEqualTo("/api/users"))
                    .withQueryParam("email", equalTo(email))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));
        } else {
            stubFor(get(urlPathEqualTo("/api/users"))
                    .withQueryParam("email", equalTo(email))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("false")));
        }
    }

    public static void reset() {
        resetAllRequests();
    }
}

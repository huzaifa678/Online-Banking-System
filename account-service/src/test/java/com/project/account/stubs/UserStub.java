package com.project.account.stubs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class UserStub {

    public static void userStubCall(String email) {
        if (email.equals("example9009@example.com")) {
            stubFor(get(urlEqualTo("/api/users"))
                    .withQueryParam("id", equalTo(email))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("true")));

        } else {
            stubFor(get(urlEqualTo("/api/users"))
                    .withQueryParam("id", equalTo(email))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("false")));
        }
    }

}

package com.project.transaction.stub;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AccountStub {
    public static void stubDoesAccountExist(String id, boolean accountExists) {
        stubFor(get(urlEqualTo("/api/accounts"))
                .withQueryParam("id", equalTo(id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(accountExists))));
    }

    public static void stubAccountBalance(String id, BigDecimal transaction, boolean isBalanceEnough) {
        stubFor(post(urlPathEqualTo("/api/accounts"))
                .withQueryParam("id", equalTo(id))
                .withQueryParam("transaction", equalTo(transaction.toString()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(isBalanceEnough))));
    }

    public static void stubCreditAccountBalance(BigDecimal amount, String id, BigDecimal creditedBalance) {
        stubFor(post(urlPathEqualTo("/api/accounts/credit"))
                .withQueryParam("amount", equalTo(amount.toString()))
                .withQueryParam("id", equalTo(String.valueOf(id)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(creditedBalance.toString())));
    }

    public static void stubDebitAccountBalance(BigDecimal amount, String id, BigDecimal debitedBalance) {
        stubFor(post(urlPathEqualTo("/api/accounts/debit"))
                .withQueryParam("amount", equalTo(amount.toString()))
                .withQueryParam("id", equalTo(id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(debitedBalance.toString())));
    }

    public static void stubIsAccountClosed(String id, boolean isClosed) {
        stubFor(post(urlPathEqualTo("api/accounts/isclosed"))
                .withQueryParam("id", equalTo(id))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(isClosed))));
    }
}

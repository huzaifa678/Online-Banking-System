package com.project.account.infrastructure.client;

import com.project.account.application.port.out.UserGatewayPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserGatewayAdapter implements UserGatewayPort {

    private final UserClient userClient;

    @Override
    public boolean doesUserExist(String email) {
        return userClient.doesUserExist(email);
    }
}

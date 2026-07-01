package com.project.account.application.port.out;

/**
 * Driven port (anti-corruption layer) to the user service. Implemented by a REST
 * client adapter in the infrastructure layer.
 */
public interface UserGatewayPort {

    boolean doesUserExist(String email);
}

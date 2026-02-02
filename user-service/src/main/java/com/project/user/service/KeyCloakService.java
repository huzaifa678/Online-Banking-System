package com.project.user.service;

import com.project.user.model.Dto.UsersDto;
import com.project.user.model.Entity.Users;
import com.project.user.model.Status;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyCloakService {

    @Value("${app.config.keycloak.realm}")
    private String keyCloakRealm;

    private final Keycloak keycloak;

    public void keycloakAddUser(UsersDto dto) {

        UserRepresentation userRepresentation = new UserRepresentation();


        userRepresentation.setUsername(dto.getEmail());
        userRepresentation.setEnabled(true);


        System.out.println("the dto has been set" + userRepresentation);


        try {
             Response response = keycloak.realm(keyCloakRealm).users().create(userRepresentation);
            if (response.getStatus() != 201) {
                String errorMessage = response.readEntity(String.class);
                System.err.println("Response Body: " + errorMessage);
                throw new RuntimeException("Failed to create user: " + response.getStatusInfo().getReasonPhrase() + " - " + errorMessage);
            }

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            throw new RuntimeException("Failed to create the user: " + e.getMessage(), e);
        }

    }

    public UserRepresentation getUserFromKeycloak(String email) {
        int attempts = 5;
        int waitTime = 1000;

        for (int i = 0; i < attempts; i++) {
            try {
                List<UserRepresentation> users = keycloak.realm(keyCloakRealm)
                        .users()
                        .search(email, true);
                if (users != null && !users.isEmpty()) {
                    return users.get(0);
                }
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted while waiting to retrieve the user", e);
            }
        }
        throw new NotFoundException("User not found: " + email);
    }

    public Status determineStatusFromKeycloakUser(UserRepresentation keycloakUser) {
        if (keycloakUser.isEnabled()) {
            return Status.APPROVED;
        } else {
            return Status.DISABLED;
        }
    }
}

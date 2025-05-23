package com.project.user;

import com.project.user.controller.UsersController;
import com.project.user.model.Dto.UsersDto;
import com.project.user.model.Status;
import com.project.user.service.KeyCloakService;
import com.project.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TestUserServiceApplication {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.3")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private KeyCloakService keyCloakService;

    @LocalServerPort
    private int port;

    @Autowired
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        reset(userService, keyCloakService);
    }

    @Test
    void testAddUser() throws Exception {
        String userJson = """
                {
                    "email": "jake789@example.com",
                    "address": "washnington, USA"
                }
                """;

        UsersDto mockResponse = new UsersDto();
        mockResponse.setUser_id(1L);
        mockResponse.setEmail("jake789@example.com");
        mockResponse.setAddress("washnington, USA");
        mockResponse.setStatus(Status.APPROVED);

        doNothing().when(keyCloakService).keycloakAddUser(any(UsersDto.class));
        when(keyCloakService.getUserFromKeycloak(anyString())).thenReturn(null);
        when(keyCloakService.determineStatusFromKeycloakUser(any())).thenReturn(Status.APPROVED);

        when(userService.addUser(any(UsersDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(userService, times(1)).addUser(any(UsersDto.class));
    }

    @Test
    void testGetUserById() throws Exception {
        long userId = 1L;
        UsersDto userDto = new UsersDto();
        userDto.setEmail("jake23@example.com");
        userDto.setAddress("USA");

        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("jake23@example.com")));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void testUpdateUser() throws Exception {
        long userId = 1L;

        UsersDto updatedUserDto = new UsersDto();
        updatedUserDto.setEmail("updates@example.com");
        updatedUserDto.setAddress("updated address");

        when(userService.updateUser(userId, updatedUserDto)).thenReturn(updatedUserDto);

        String userJson = """
            {
                "email": "updates@example.com",
                "address": "updated address"
            }
            """;

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("updates@example.com")))
                .andExpect(jsonPath("$.address", is("updated address")));
        verify(userService, times(1)).updateUser(userId, updatedUserDto);

    }

    @Test
    void testDeleteUser() throws Exception {
        long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

}

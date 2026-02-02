package com.project.user.service;

import com.project.user.exceptions.DuplicateEmailException;
import com.project.user.exceptions.UserNotCreatedException;
import com.project.user.model.Dto.UsersDto;
import com.project.user.model.Entity.Users;
import com.project.user.model.Mapper.UserMapper;
import com.project.user.model.Status;
import com.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final KeyCloakService keyCloakService;

    public UsersDto addUser(UsersDto usersDto) throws InterruptedException {
        System.out.println("Received DTO: " + usersDto);

        usersDto.setStatus(Status.PLEASE_WAIT);

        if (userRepository.findByemail(usersDto.getEmail()) != null) {
            throw new DuplicateEmailException("The email you entered already exists in the database");
        }

        try {
            keyCloakService.keycloakAddUser(usersDto);
        } catch (Exception e) {
            throw new UserNotCreatedException("Failed to create the user: " + "Error: " + e);
        }

        Thread.sleep(1000);

        UserRepresentation userRepresentation = keyCloakService.getUserFromKeycloak(usersDto.getEmail());

        Status status = keyCloakService.determineStatusFromKeycloakUser(userRepresentation);

        usersDto.setStatus(status);

        Users user = userMapper.ConvertToEntity(usersDto);

        System.out.println("Received Entity: " + usersDto);

        userRepository.save(user);

        return userMapper.ConvertToDto(user);
    }

    public UsersDto getUserById(long userId) {

        Optional<Users> userOptional = userRepository.findById(userId);

        try {
            if (userOptional.isPresent()) {
                return userMapper.ConvertToDto(userOptional.get());
            } else {
                throw new RuntimeException("User ID " + userId + " not found.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find the user ID: " + userId + "error occurred: " + e);
        }


    }

    public List<UsersDto> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::ConvertToDto)
                .collect(Collectors.toList());
    }


    public UsersDto updateUser(long userId, UsersDto userDto) {
        Optional<Users> userOptional = userRepository.findById(userId);

        try {
            if (userOptional.isPresent()) {
                Users user = userOptional.get();

                user.setEmail(userDto.getEmail());
                user.setAddress(userDto.getAddress());

                userRepository.save(user);

                return userMapper.ConvertToDto(user);
            } else {
                throw new RuntimeException("Failed to update user with ID: " + userId + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update the user ID: " + userId + "error occurred: " + e);
        }

    }

    public void deleteUser(long userId) {
        Optional<Users> userOptional = userRepository.findById(userId);

        try {
            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
            } else {
                throw new RuntimeException("Failed to delete user with ID: " + userId + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the User ID: " + userId + "error occurred: " + e);
        }

    }

    public boolean doesUserExist(String email) {
        boolean ifUserExist = userRepository.existsByemail(email);
        return ifUserExist;
    }
}


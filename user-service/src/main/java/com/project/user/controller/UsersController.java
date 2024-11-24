package com.project.user.controller;

import com.project.user.model.Dto.UsersDto;
import com.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    @Autowired
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> addUser(@RequestBody UsersDto userDto) {
        System.out.println("Received request body: " + userDto);
        try {
            UsersDto dto = userService.addUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto.getUser_id());
        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            throw new RuntimeException("An error occurred while adding the user: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsersDto> getUserById(@PathVariable long id) {

        try {
            UsersDto userDto = userService.getUserById(id);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            throw new RuntimeException("Could not find the user with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @GetMapping("/allusers")
    public ResponseEntity<List<UsersDto>> getAllUsers() {
        try {
            List<UsersDto> usersList = userService.getAllUsers();
            return ResponseEntity.ok(usersList);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve users: " + e.getMessage(), e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsersDto> updateUser(@PathVariable long id, @RequestBody UsersDto userDto) {
        try {
            UsersDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update the user with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete the user with ID " + id + ": " + e.getMessage(), e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean doesUserExist(@RequestParam String email) {
        return userService.doesUserExist(email);
    }
}

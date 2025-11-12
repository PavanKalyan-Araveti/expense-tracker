package com.pavan.expensetracker.controller;

import com.pavan.expensetracker.dto.UserRequest;
import com.pavan.expensetracker.dto.UserResponse;
import com.pavan.expensetracker.model.User;
import com.pavan.expensetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest){
        var savedUser = userService.registerUser(
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getFullName()
        );
        UserResponse response = UserResponse.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .fullName(savedUser.getFullName())
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest userRequest){
        try {
            User user = userService.login(
                    userRequest.getUsername(),
                    userRequest.getPassword()
            );
            UserResponse response = UserResponse.builder()
                    .id(user.getId())
                    .userName(user.getFullName())
                    .fullName(user.getFullName())
                    .build();
            return ResponseEntity.ok(response);
        }
        catch(IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Message", ex.getMessage()));
        }
    }
}

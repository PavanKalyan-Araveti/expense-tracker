package com.pavan.expensetracker.controller;

import com.pavan.expensetracker.dto.AuthResponse;
import com.pavan.expensetracker.dto.UserRequest;
import com.pavan.expensetracker.dto.UserResponse;
import com.pavan.expensetracker.exception.InvalidCredentialsException;
import com.pavan.expensetracker.model.User;
import com.pavan.expensetracker.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
            String token = jwtUtil.generateToken(user.getUserName());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        catch(InvalidCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Message", ex.getMessage()));
        }
    }
}

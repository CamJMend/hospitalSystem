package com.hospital.auth.controller;

import com.hospital.auth.model.User;
import com.hospital.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = authService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            log.error("Registration failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            String token = authService.login(email, password);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            boolean isValid = authService.validateToken(token);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", isValid);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", false);
            return ResponseEntity.ok(response);
        }
    }
}
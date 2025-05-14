package com.hospital.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.hospital.auth.model.User;
import com.hospital.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final JwtUtil jwtUtil;
    
    public User createUser(User user) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setDisplayName(user.getFirstName() + " " + user.getLastName());
        
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        
        // Set custom claims for role
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
        
        user.setUid(userRecord.getUid());
        user.setPassword(null); // Don't return password
        
        log.info("User created with UID: {}", userRecord.getUid());
        return user;
    }
    
    public String login(String email, String password) throws FirebaseAuthException {
        try {
            // En producción, verificarías con Firebase Auth
            // Aquí simplificamos para el prototipo
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            
            // Obtener role desde custom claims
            String role = (String) userRecord.getCustomClaims().get("role");
            
            // Generar JWT token
            String token = jwtUtil.generateToken(userRecord.getUid(), role);
            
            log.info("User {} logged in successfully", email);
            return token;
            
        } catch (FirebaseAuthException e) {
            log.error("Login failed for user: {}", email, e);
            throw new RuntimeException("Invalid credentials", e);
        }
    }
    
    public boolean validateToken(String token) {
        try {
            String uid = jwtUtil.extractUsername(token);
            return !jwtUtil.isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
}
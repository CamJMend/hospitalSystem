package com.hospital.auth.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.hospital.auth.model.User;
import com.hospital.auth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private static final String USERS_COLLECTION = "users";
    
    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    public User createUser(User user) throws ExecutionException, InterruptedException {
        String uid = UUID.randomUUID().toString();
        user.setUid(uid);
        Firestore firestore = FirestoreClient.getFirestore();
        firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(user)
                .get();
        user.setPassword(null);
        
        log.info("User created with UID: {}", uid);
        return user;
    }
    
    public String login(String email, String password) throws ExecutionException, InterruptedException {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            
            var querySnapshot = firestore.collection(USERS_COLLECTION)
                    .whereEqualTo("email", email)
                    .get()
                    .get();
            
            if (querySnapshot.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            User user = querySnapshot.getDocuments().get(0).toObject(User.class);
            if (!password.equals(user.getPassword())) {
                throw new RuntimeException("Contraseña incorrecta");
            }
            
            String token = jwtUtil.generateToken(user.getUid(), user.getRole());
            
            log.info("User {} logged in successfully", email);
            return token;
            
        } catch (Exception e) {
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
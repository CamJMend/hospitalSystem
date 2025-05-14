package com.hospital.patient.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleUserDetailsService implements UserDetailsService {

    // This is a simple in-memory user store for demonstration purposes
    // In a real application, this would be replaced with a repository that fetches users from a database
    private final Map<String, SimpleUserDetails> users = new HashMap<>();

    public SimpleUserDetailsService() {
        // Initialize with some test users
        users.put("admin", new SimpleUserDetails("admin", "admin", 
                Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        
        users.put("doctor", new SimpleUserDetails("doctor", "doctor", 
                Arrays.asList(new SimpleGrantedAuthority("ROLE_DOCTOR"))));
        
        users.put("patient", new SimpleUserDetails("patient", "patient", 
                Arrays.asList(new SimpleGrantedAuthority("ROLE_PATIENT"))));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SimpleUserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        return new User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
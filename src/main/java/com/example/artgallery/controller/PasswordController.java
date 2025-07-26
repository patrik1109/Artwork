package com.example.artgallery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = {"http://localhost:3000", "https://artwork-production-fec7.up.railway.app"})
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/hash")
    public ResponseEntity<?> generateHash(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            String hash = passwordEncoder.encode(password);
            boolean matches = passwordEncoder.matches(password, hash);

            Map<String, Object> response = new HashMap<>();
            response.put("password", password);
            response.put("hash", hash);
            response.put("matches", matches);
            response.put("message", "Hash generated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating hash: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        try {
            String password = request.get("password");
            String hash = request.get("hash");
            
            if (password == null || hash == null) {
                return ResponseEntity.badRequest().body("Password and hash are required");
            }

            boolean matches = passwordEncoder.matches(password, hash);

            Map<String, Object> response = new HashMap<>();
            response.put("password", password);
            response.put("hash", hash);
            response.put("matches", matches);
            response.put("message", matches ? "Password matches hash" : "Password does not match hash");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying password: " + e.getMessage());
        }
    }
} 
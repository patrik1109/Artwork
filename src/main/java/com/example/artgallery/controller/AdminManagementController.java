package com.example.artgallery.controller;

import com.example.artgallery.entity.Admin;
import com.example.artgallery.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin-management")
@CrossOrigin(origins = {"http://localhost:3000", "https://artwork-production-fec7.up.railway.app"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String role = request.get("role");

            if (username == null || password == null || role == null) {
                return ResponseEntity.badRequest().body("Username, password and role are required");
            }

            Admin admin = adminAuthService.createAdmin(username, password, role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin created successfully");
            response.put("username", admin.getUsername());
            response.put("role", admin.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating admin: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (username == null || oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Username, old password and new password are required");
            }

            boolean success = adminAuthService.changePassword(username, oldPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid old password");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error changing password: " + e.getMessage());
        }
    }

    @PostMapping("/disable/{username}")
    public ResponseEntity<?> disableAdmin(@PathVariable String username) {
        try {
            adminAuthService.disableAdmin(username);
            return ResponseEntity.ok("Admin disabled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error disabling admin: " + e.getMessage());
        }
    }

    @PostMapping("/enable/{username}")
    public ResponseEntity<?> enableAdmin(@PathVariable String username) {
        try {
            adminAuthService.enableAdmin(username);
            return ResponseEntity.ok("Admin enabled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error enabling admin: " + e.getMessage());
        }
    }
} 
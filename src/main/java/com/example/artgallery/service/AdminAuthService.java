package com.example.artgallery.service;

import com.example.artgallery.entity.Admin;
import com.example.artgallery.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class AdminAuthService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsernameAndEnabledTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        // Оновлюємо час останнього входу
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + admin.getRole())))
                .accountExpired(false)
                .accountLocked(!admin.isEnabled())
                .credentialsExpired(false)
                .disabled(!admin.isEnabled())
                .build();
    }

    public Admin createAdmin(String username, String rawPassword, String role) {
        if (adminRepository.existsByUsername(username)) {
            throw new RuntimeException("Admin with username " + username + " already exists");
        }

        Admin admin = new Admin(username, passwordEncoder.encode(rawPassword), role);
        return adminRepository.save(admin);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminRepository.save(admin);
            return true;
        }
        return false;
    }

    // Метод для зміни пароля без перевірки старої (тільки для адміністраторів)
    public boolean forceChangePassword(String username, String newPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        return true;
    }

    public void disableAdmin(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
        admin.setEnabled(false);
        adminRepository.save(admin);
    }

    public void enableAdmin(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
        admin.setEnabled(true);
        adminRepository.save(admin);
    }
} 
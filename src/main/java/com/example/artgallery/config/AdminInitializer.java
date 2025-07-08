package com.example.artgallery.config;

import com.example.artgallery.service.AdminAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private AdminAuthService adminAuthService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Створюємо першого адміна, якщо його ще немає
            adminAuthService.createAdmin("admin", "patrik_1109", "ADMIN");
            logger.info("Default admin user created successfully");
        } catch (Exception e) {
            logger.info("Admin user already exists or error occurred: {}", e.getMessage());
        }
    }
} 
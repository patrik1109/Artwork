package com.example.artgallery.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Приклади паролів для хешування
        String[] passwords = {
            "admin123",
            "новий_пароль_123",
            "super_secret_password",
            "test123"
        };
        
        System.out.println("=== BCrypt хеші паролів ===");
        System.out.println();
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Пароль: " + password);
            System.out.println("Хеш: " + hash);
            System.out.println("Перевірка: " + encoder.matches(password, hash));
            System.out.println("---");
        }
        
        // Генерація хешу для конкретного пароля
        System.out.println("=== Генерація хешу для твого пароля ===");
        System.out.println("Введи пароль в код нижче і запусти main метод:");
        System.out.println();
        System.out.println("String myPassword = \"твій_пароль_тут\";");
        System.out.println("String hash = encoder.encode(myPassword);");
        System.out.println("System.out.println(\"Хеш: \" + hash);");
    }
} 
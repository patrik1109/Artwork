package com.example.artgallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    // UserDetailsService буде інжектований автоматично

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Spring Security...");
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").authenticated()
                .requestMatchers("/api/admin-management/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> {})
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setHeader("WWW-Authenticate", "Basic realm=\"Admin Access\"");
                    response.getWriter().write("Authentication required");
                })
            );
        logger.info("Spring Security configured successfully");
        return http.build();
    }

    // UserDetailsService тепер реалізований в AdminAuthService

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 
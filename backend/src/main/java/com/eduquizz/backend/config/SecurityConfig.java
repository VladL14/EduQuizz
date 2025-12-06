package com.eduquizz.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Dezactivăm protecția CSRF
            .authorizeHttpRequests(auth -> auth
                // 1. REGULI SPECIFICE (Permite Swagger și API)
                // Acestea TREBUIE să fie primele!
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api/**", // Permitem tot ce e sub /api/ (inclusiv Users, Auth)
                    "/error"
                ).permitAll()

                .requestMatchers(
                    HttpMethod.OPTIONS,"/**"
                ).permitAll()
                
                // 2. REGULA GENERALĂ (Blochează restul)
                // Aceasta TREBUIE să fie ultima!
                .anyRequest().authenticated()
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.enigma.wmb_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

//#SPRING SECURITY# 09
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // annotation ini ada untuk mengamankan method2 spesifik di aplikasi kita
// nah ini memungkinkan kita untuk menggunakan anonnotation2 security lainnya
// misal @PreAuthorize di method-method kita (lebih baik awal masuk/entry point, di controller)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> {
                    config.authenticationEntryPoint(customAuthEntryPoint);
                    config.accessDeniedHandler(customAccessDeniedHandler);
                })
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/auth/register", "/api/v1/auth/register-super-admin").permitAll()
                                .requestMatchers("/api/v1/posts/**", "/api/v1/todos", "/api/v1/todos/**").permitAll()
                                .requestMatchers("/api/v1/payments/notifications").permitAll()
                                .requestMatchers("/api/v1/images", "/api/v1/images/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

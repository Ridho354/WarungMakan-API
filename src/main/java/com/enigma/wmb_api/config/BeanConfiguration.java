package com.enigma.wmb_api.config;

import com.enigma.wmb_api.security.JwtAuthenticationFilter;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

// #SPRING SECURITY# 06 kita tambahkan di bean config atau config apapun (@Configuration)
// 06a tambahkan bean passwordEncoder (PasswordEncoder interfacenya, implementasinya BCryptPasswordEncoder)
// 06b tambahkan bean UserDetailsService userDetailsService(), yang dimana ini bertugas nanti untuk mengambil
// data user dari database (misal ketika login, service ini akan mencari: ada gak sih user dengan username ini)
// 06c tambahkan bean AuthenticationManager authenticationManager (sebagai manager/kordinator yang mengatur proses autentikasi secara keseluruhan)
// mulai dari terima kredensial user, memverifikasi credentials (catatan dengan authenticationProvider), dan apakah authenticated
// 06d tambahkan bean authenticationProvider() bisa dari misal DaoAuthenticationProvider
// komponen ini nanti akan menangani autentikasi (memverifikasi credentials tadi), memverifikasi apakah username dan passwordnya sesuai
// di project kita menggunakan DaoAuthenticationProvider yang kita setUserDetailsService dengan userDetailsService() tadi authProvider.setUserDetailsService(userDetailsService());
// dan juga kita set password encodernya authProvider.setPasswordEncoder(passwordEncoder());
// add bean JwtAuthenticationFilter jwtAuthenticationFilter()

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final UserService userService;
    private final JwtService jwtService;

    @Bean public UserDetailsService userDetailsService() {
        return (username -> userService.loadUserByUsername(username));
//        return userService; alternativenya
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userService);
    }

    // #SPRING SECURITY# 13, class library dari Jackson yang akan menghandle conversi JSON/Object
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
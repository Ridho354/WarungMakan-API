package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.UserRepository;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserAccount getByUserId(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserAccount getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserAccount createUser(UserAccount userAccount) {
        return userRepository.saveAndFlush(userAccount);
    }

    @Override
    public UserAccount getByContext() {
        // #SPRING SECURITY# 11 ini ngambil userAccount dari Spring Security Context, supaya
        // gak perlu decode lagi dan gak perlu hit database lagi untuk mendapatkan userAccount
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // inget JwtAuthenticationFilter SecurityContextHolder.getContext().setAuthentication
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        return userAccount;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> user = userRepository.findByUsername(username);
        System.out.println("loadUserByUsername" + user);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}

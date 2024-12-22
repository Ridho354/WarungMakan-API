package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // dari sini
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// #SPRING SECURITY# 05 buat Service misal UserService yang extend UserDetailsService
public interface UserService extends UserDetailsService {
    UserAccount getByUserId(String userId);
    UserAccount getByUsername(String username);
    UserAccount createUser(UserAccount userAccount);
    UserAccount getByContext();
    // optional tambahin dibawah ini (walaupun sebenernya ga usah karena sudah extends)
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

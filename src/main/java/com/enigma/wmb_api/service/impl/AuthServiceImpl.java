package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.request.RegisterCustomerRequest;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final CustomerService customerService;
    @Value("${wmb_api.secret.super_admin_header_key}")
    private String superAdminHeaderKey;

    @Override
//    @Transactional // by default atau kosongan itu @Transactional dia akan rollback ketika mostly unchecked exceptions
    @Transactional(rollbackFor = Exception.class) // nah kalau ditambah rollbackFor = Exception.class maka ia akan rollback ketika exception ada, entah checked exception ataupun unchecked exception
    public RegisterResponse register(RegisterCustomerRequest request) {
        validateUsernameNotExists(request.getUsername());

        UserAccount userAccount = toUserAccount(request.getUsername(), request.getPassword(), UserRole.ROLE_CUSTOMER);
        UserAccount savedUserAccount = userService.createUser(userAccount); // create user account dahulu

        customerService.createCustomer(CustomerRequest.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .userAccount(savedUserAccount)
                .build()); // create customer kemudian

        return toRegisterResponse(userAccount);
    }

    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        validateUsernameNotExists(request.getUsername());

        UserAccount userAccount = toUserAccount(request.getUsername(), request.getPassword(), UserRole.ROLE_ADMIN);
        userService.createUser(userAccount);

        return toRegisterResponse(userAccount);
    }

    @Override
    public RegisterResponse registerSuperAdmin(AuthRequest request, String superAdminRequestHeader) {
        validateSuperAdminHeader(superAdminRequestHeader);
        validateUsernameNotExists(request.getUsername());

        UserAccount userAccount = toUserAccount(request.getUsername(), request.getPassword(), UserRole.ROLE_SUPER_ADMIN);
        userService.createUser(userAccount);

        return toRegisterResponse(userAccount);
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        // #SPRING SECURITY# 07 karena kita menerapkan prinsip IoC (Inversion of Control)
        // maka proses authentikasi kita serahkan pada framework spring security
        // kalau mau manual sebenernya bisa2 aja dan gak ada yang melarang misal di manualValidateCredentialsAndGetUserAccount
        // proses authentikasi ada di authenticationManager.authenticate (dibalik layar sebenernya yang verifikasi si authenticationProvider)
        // tapi authenticationManager.authenticate butuh argumen/parameter dengan tipe data UsernamePasswordAuthenticationToken
        // cara buat UsernamePasswordAuthenticationToken gimana? misal new UsernamePasswordAuthenticationToken(username, password)
        // authenticationManager.authenticate akan menghasilan Authentication object (Authentication authentication)
        // authentication.getPrincipal() akan menghasilkan data seperti UserAccount
        // yang akan kita gunakan menjadi bahan untuk generate token
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                usernamePasswordAuthenticationToken
        );

        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        if (!authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constant.ERROR_INVALID_CREDENTIALS);
        }

//        UserAccount userAccount = manualValidateCredentialsAndGetUserAccount(request);
//
        String generatedToken = jwtService.generateToken(userAccount);

        return LoginResponse.builder()
                .username(userAccount.getUsername())
                .token(generatedToken)
                .roles(String.valueOf(userAccount.getRole()))
                .build();
    }

    private void validateUsernameNotExists(String username) {
        if (userService.getByUsername(username) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, Constant.ERROR_USERNAME_EXISTS);
        }
    }

    private void validateSuperAdminHeader(String superAdminRequestHeader) {
        if (!superAdminHeaderKey.equals(superAdminRequestHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constant.ERROR_INVALID_SUPER_ADMIN_HEADER);
        }
    }

    private UserAccount toUserAccount(String username, String password, UserRole role) {
        return UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
    }

    private RegisterResponse toRegisterResponse(UserAccount userAccount) {
        return RegisterResponse.builder()
                .username(userAccount.getUsername())
                .role(String.valueOf(userAccount.getRole()))
                .build();
    }

    private UserAccount manualValidateCredentialsAndGetUserAccount(AuthRequest request) {
        UserAccount userAccount = userService.getByUsername(request.getUsername());

        if (userAccount != null){
            boolean isValid = passwordEncoder.matches(request.getPassword(), userAccount.getPassword());

            if (isValid){
                return userAccount;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constant.ERROR_INVALID_CREDENTIALS);
    }
}

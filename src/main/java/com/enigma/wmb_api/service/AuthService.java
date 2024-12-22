package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.request.RegisterCustomerRequest;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterCustomerRequest request);
    RegisterResponse registerAdmin(AuthRequest request);
    RegisterResponse registerSuperAdmin(AuthRequest request, String superAdminRequestHeader);
    LoginResponse login(AuthRequest request);
}

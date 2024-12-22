package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.repository.CustomerRepository;
import com.enigma.wmb_api.service.PermissionEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// #SPRING SECURITY# 12
@Service
@RequiredArgsConstructor
public class PermissionEvaluationServiceImpl implements PermissionEvaluationService {
    private final CustomerRepository customerRepository;

    @Override
    public boolean hasAccessToCustomer(String customerId, UserAccount userAccount) {
        return customerRepository.existsByIdAndUserAccount_id(customerId, userAccount.getId());
    }
}

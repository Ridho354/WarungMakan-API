package com.enigma.wmb_api.service;

import com.enigma.wmb_api.entity.UserAccount;

public interface PermissionEvaluationService {
    public boolean hasAccessToCustomer(String customerId, UserAccount userAccount);
}

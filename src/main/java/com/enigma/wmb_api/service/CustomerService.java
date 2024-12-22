package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.CustomerRequest;
import com.enigma.wmb_api.dto.response.CustomerResponse;
import com.enigma.wmb_api.entity.Customer;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest customerRequest);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(String customerId);
    Customer getOne(String id);
    CustomerResponse updateCustomer(String customerId, CustomerRequest customerRequest);
    void deleteCustomer(String customerId);
}

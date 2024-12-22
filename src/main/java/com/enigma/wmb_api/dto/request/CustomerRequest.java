package com.enigma.wmb_api.dto.request;

import com.enigma.wmb_api.entity.UserAccount;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    private String name;

    @Email(message = "Must be a valid email address")
    private String email;
    private String phoneNumber;
    private UserAccount userAccount;
}

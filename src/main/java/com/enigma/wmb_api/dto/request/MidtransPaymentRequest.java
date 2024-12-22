package com.enigma.wmb_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// #Midtrans# buat MidtransResponseResponseDTO https://docs.midtrans.com/docs/snap-snap-integration-guide Successful Sample Response
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransPaymentRequest {
    @JsonProperty(value = "transaction_details")
    MidtransTransactionDetailRequest transactionDetail;

    @JsonProperty(value = "enabled_payments")
    private List<String> enabledPayments;
}

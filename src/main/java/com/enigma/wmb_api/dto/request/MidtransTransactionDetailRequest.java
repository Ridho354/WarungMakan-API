package com.enigma.wmb_api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
*
* "order_id": "YOUR-ORDERID-123456",
        "gross_amount": 10000
* */
// #Midtrans# buat MidtransResponseResponseDTO https://docs.midtrans.com/docs/snap-snap-integration-guide Successful Sample Response
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransTransactionDetailRequest {
    @JsonProperty(value = "order_id")
    private String orderId;

    @JsonProperty(value = "gross_amount")
    private Long grossAmount;
}

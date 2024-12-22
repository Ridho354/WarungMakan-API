package com.enigma.wmb_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// #Midtrans# buat MidtransResponseResponseDTO https://docs.midtrans.com/docs/snap-snap-integration-guide Successful Sample Response
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MidtransSnapResponse {
    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "redirect_url")
    private String redirectUrl;
}

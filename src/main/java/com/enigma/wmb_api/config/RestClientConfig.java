package com.enigma.wmb_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.Base64;


// #REST CLIENT#
// Kenapa butuh REST CLIENT? Di dunia nyata aplikasi (Rest API) jarang berdiri sendiri,
// Misal di aplikasi bandara, ada jadwal penerbangan (itu ambilnya REST API aplikasi bandaranya)
// Misal mau juga tampilin informasi cuaca, apakah harus develop informasi cuaca (invest alat-alat untuk cuaca, dsb) kan engga
// itu udah diurus oleh organisasi dan sudah aplikasinya
// Jadi perlu komunikasi antara satu aplikasi dengan aplikasi lain
// contoh lain adalah pembayaran, kita fokus di inti aplikasi kita yaitu warung makan, dan masalah pembayaran
// pembayaran kita serahkan pada aplikasi lain, misal aplikasi payment gateway
// untuk komunikasi butuh rest client
// ibarat Pabrik dan Kita Customer butuh perantara ketika pesan barang, misal Kurir sebagai perantara
// nah rest client itu ibarat kurirnya, yang menerima permintaan dan membawa data dari satu aplikasi ke aplikasi lain
@Configuration
public class RestClientConfig {
    @Value("${wmb_api.json_placeholder_url}")
    private String jsonPlaceholderBaseUrl;

    @Value("${midtrans.api.url}")
    private String midtransBaseApiUrl;

    @Value("${midtrans.server.key}")
    private String midtransServerKey;

    @Bean("jsonPlaceHolderClient")
    public RestClient jsonPlaceHolderClient() {
        return RestClient.builder().baseUrl(jsonPlaceholderBaseUrl).defaultHeader("Content-Type", "application/json").build();
    }


    @Bean("midtransPaymentGatewayClient")
    public RestClient midtransPaymentGatewayClient() {
        Base64.Encoder base64Encoder = Base64.getEncoder();
        // https://docs.midtrans.com/docs/snap-snap-integration-guide
        // AUTH_STRING: Base64Encode("YourServerKey"+":")
        String AUTH_STRING = base64Encoder.encodeToString((midtransServerKey + ":").getBytes());
        // Authorization	Basic AUTH_STRING
        String authorizationHeader = "Basic " + AUTH_STRING;

        return RestClient.builder()
                .baseUrl(midtransBaseApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

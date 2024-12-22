package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.MidtransNotificationRequest;
import com.enigma.wmb_api.dto.response.PaymentResponse;

// #Midtrans#
public interface PaymentService {
    PaymentResponse createPayment(String orderId);
    void handlePaymentNotification(MidtransNotificationRequest request); // di hit oleh midtrans, dengan mekanisme WebHook, ketika misal pembayaran telah berhasil
}

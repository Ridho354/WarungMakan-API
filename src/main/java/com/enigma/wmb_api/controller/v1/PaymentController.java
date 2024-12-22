package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.MidtransNotificationRequest;
import com.enigma.wmb_api.dto.response.PaymentResponse;
import com.enigma.wmb_api.service.PaymentService;
import com.enigma.wmb_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(Constant.PAYMENT_API)
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/checkout/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> checkout(@PathVariable("orderId") String orderId) {
        PaymentResponse createdPaymentResponse = paymentService.createPayment(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Payment created", createdPaymentResponse);
    }

    @PostMapping("/notifications")
    // permasalahannya di tipe data gak bisa MidtransNotificationRequest, karena dari midtransnya
    // tipe datanya sangat berbeda-beda
    // jadi bisa diakalin dengan Object atau Hashmap
    public ResponseEntity<?> handleNotification(@RequestBody Map<String, Object> notificationRequest) {

        MidtransNotificationRequest midtransNotificationRequest = MidtransNotificationRequest.builder()
                .orderId((String) notificationRequest.get("order_id"))
                .statusCode((String) notificationRequest.get("status_code"))
                .grossAmount((String) notificationRequest.get("gross_amount"))
                .signatureKey((String) notificationRequest.get("signature_key"))
                .transactionStatus((String) notificationRequest.get("transaction_status"))
                .transactionTime((String) notificationRequest.get("transaction_time"))
                .build();
        paymentService.handlePaymentNotification(midtransNotificationRequest);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Notification successfully processed", null);
    }
}

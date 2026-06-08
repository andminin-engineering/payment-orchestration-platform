package com.andminin.payment.api.controller;

import com.andminin.payment.application.service.CreatePaymentRequest;
import com.andminin.payment.application.service.CreatePaymentResponse;
import com.andminin.payment.application.service.GetPaymentStatusResponse;
import com.andminin.payment.application.service.PaymentService;
import com.andminin.payment.domain.port.PaymentProviderException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(com.andminin.payment.api.error.GlobalExceptionHandler.class)
@DisplayName("PaymentController Tests")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /v1/payments responde 201 con body")
    void createPaymentReturns201() throws Exception {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
            .amount(new BigDecimal("150.00"))
            .currency("USD")
            .customerEmail("customer@example.com")
            .cardToken("tok_test")
            .idempotencyKey("idem-http-1")
            .build();

        when(paymentService.createPayment(any())).thenReturn(
            new CreatePaymentResponse("pay-123", "AUTHORIZED", "tx-789")
        );

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentId").value("pay-123"))
            .andExpect(jsonPath("$.status").value("AUTHORIZED"))
            .andExpect(jsonPath("$.providerTransactionId").value("tx-789"));
    }

    @Test
    @DisplayName("POST /v1/payments invalido responde 400")
    void createPaymentInvalidBodyReturns400() throws Exception {
        String invalidBody = """
            {
              \"currency\": \"USD\",
              \"customerEmail\": \"customer@example.com\",
              \"cardToken\": \"tok_test\",
              \"idempotencyKey\": \"idem-http-2\"
            }
            """;

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("ValidationError"));
    }

    @Test
    @DisplayName("POST /v1/payments/{id}/capture provider error responde 502")
    void captureProviderErrorReturns502() throws Exception {
        when(paymentService.capturePayment(any())).thenThrow(
            new PaymentProviderException("upstream down", "UPSTREAM_ERR", "DUMMY")
        );

        mockMvc.perform(post("/api/v1/payments/payment-1/capture"))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.error").value("PaymentProviderError"));
    }

    @Test
    @DisplayName("GET /v1/payments/{id} responde 200")
    void getPaymentStatusReturns200() throws Exception {
        when(paymentService.getPaymentStatus(eq("payment-200"))).thenReturn(
            new GetPaymentStatusResponse("payment-200", "COMPLETED", "100.00 USD", "tx-200")
        );

        mockMvc.perform(get("/api/v1/payments/payment-200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value("payment-200"))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}

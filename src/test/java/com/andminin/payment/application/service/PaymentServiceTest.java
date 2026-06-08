package com.andminin.payment.application.service;

import com.andminin.payment.domain.model.Money;
import com.andminin.payment.domain.model.Payment;
import com.andminin.payment.domain.model.PaymentId;
import com.andminin.payment.domain.port.PaymentAuthorizationResponse;
import com.andminin.payment.domain.port.PaymentCaptureResponse;
import com.andminin.payment.domain.port.PaymentProvider;
import com.andminin.payment.domain.port.PaymentProviderException;
import com.andminin.payment.domain.port.PaymentRefundResponse;
import com.andminin.payment.domain.port.PaymentRepository;
import com.andminin.payment.domain.port.ProviderAuthorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProvider paymentProvider;

    @Mock
    private ProviderAuthorization providerAuthorization;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        when(paymentProvider.getProviderName()).thenReturn("DUMMY");
    }

    @Test
    @DisplayName("createPayment retorna existente por idempotency key")
    void createPaymentReturnsExistingPayment() throws Exception {
        CreatePaymentRequest request = createRequest("idem-001");

        Payment existing = Payment.builder()
            .id(new PaymentId("p-1"))
            .amount(new Money(new BigDecimal("120.00"), "USD"))
            .customerEmail("customer@example.com")
            .paymentProvider("DUMMY")
            .idempotencyKey("idem-001")
            .build();
        existing.authorize("tx-existing");

        when(paymentRepository.findByIdempotencyKey("idem-001")).thenReturn(Optional.of(existing));

        CreatePaymentResponse response = paymentService.createPayment(request);

        assertEquals("p-1", response.getPaymentId());
        assertEquals("AUTHORIZED", response.getStatus());
        assertEquals("tx-existing", response.getProviderTransactionId());
        verify(paymentProvider, never()).authorize(any(), any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPayment nuevo autoriza y persiste")
    void createPaymentAuthorizesAndPersists() throws Exception {
        CreatePaymentRequest request = createRequest("idem-002");

        when(paymentRepository.findByIdempotencyKey("idem-002")).thenReturn(Optional.empty());
        when(paymentProvider.authorize(any(), any())).thenReturn(PaymentAuthorizationResponse.success("tx-new"));

        CreatePaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response.getPaymentId());
        assertEquals("AUTHORIZED", response.getStatus());
        assertEquals("tx-new", response.getProviderTransactionId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("capturePayment exitoso deja estado COMPLETED")
    void capturePaymentMovesToCompleted() throws Exception {
        Payment payment = authorizedPayment("payment-cap-1", "tx-cap-1");

        when(paymentRepository.findById(new PaymentId("payment-cap-1"))).thenReturn(Optional.of(payment));
        when(paymentProvider.capture(any(), anyString())).thenReturn(PaymentCaptureResponse.success());

        CapturePaymentResponse response = paymentService.capturePayment(new CapturePaymentRequest("payment-cap-1"));

        assertEquals("payment-cap-1", response.getPaymentId());
        assertEquals("COMPLETED", response.getStatus());
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("refundPayment exitoso retorna refundId")
    void refundPaymentReturnsRefundId() throws Exception {
        Payment payment = completedPayment("payment-ref-1", "tx-ref-1");

        when(paymentRepository.findById(new PaymentId("payment-ref-1"))).thenReturn(Optional.of(payment));
        when(paymentProvider.refund(any(), anyString())).thenReturn(PaymentRefundResponse.success("refund-123"));

        RefundPaymentResponse response = paymentService.refundPayment(new RefundPaymentRequest("payment-ref-1"));

        assertEquals("payment-ref-1", response.getPaymentId());
        assertEquals("REFUNDED", response.getStatus());
        assertEquals("refund-123", response.getRefundId());
        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("createPayment invalido lanza IllegalArgumentException")
    void createPaymentInvalidInputFails() {
        CreatePaymentRequest request = createRequest("idem-003");
        request.setAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> paymentService.createPayment(request));
    }

    private CreatePaymentRequest createRequest(String idempotencyKey) {
        return CreatePaymentRequest.builder()
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .customerEmail("customer@example.com")
            .cardToken("tok_test_123")
            .idempotencyKey(idempotencyKey)
            .build();
    }

    private Payment authorizedPayment(String paymentId, String providerTxId) {
        Payment payment = Payment.builder()
            .id(new PaymentId(paymentId))
            .amount(new Money(new BigDecimal("80.00"), "USD"))
            .customerEmail("customer@example.com")
            .paymentProvider("DUMMY")
            .idempotencyKey("idem-" + paymentId)
            .build();
        payment.authorize(providerTxId);
        return payment;
    }

    private Payment completedPayment(String paymentId, String providerTxId) {
        Payment payment = authorizedPayment(paymentId, providerTxId);
        payment.capture();
        payment.complete();
        return payment;
    }
}

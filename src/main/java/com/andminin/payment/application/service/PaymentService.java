package com.andminin.payment.application.service;

import com.andminin.payment.domain.model.Money;
import com.andminin.payment.domain.model.Payment;
import com.andminin.payment.domain.model.PaymentId;
import com.andminin.payment.domain.port.PaymentAuthorizationRequest;
import com.andminin.payment.domain.port.PaymentAuthorizationResponse;
import com.andminin.payment.domain.port.PaymentProvider;
import com.andminin.payment.domain.port.PaymentProviderException;
import com.andminin.payment.domain.port.PaymentRepository;
import com.andminin.payment.domain.port.ProviderAuthorization;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Servicio de Aplicación: PaymentService
 * 
 * Orquesta el flujo de pagos usando el dominio (Payment aggregate).
 * Implementa casos de uso como:
 * - Crear y autorizar un pago
 * - Capturar un pago autorizado
 * - Reembolsar un pago
 * 
 * Contexto arquitectónico:
 * - ADR-001: Aislamiento de dominio mediante inyección de puertos
 * - ADR-003: Idempotencia: mismo request = mismo resultado
 * - C4 Component: Orquestación de pagos y aplicación de reglas
 * 
 * Flujo típico:
 * 1. createPayment(request) → crea agregado Payment, persiste, autoriza con proveedor
 * 2. Si timeout → cliente reintentar → mismo idempotencyKey → retorna resultado anterior
 */
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final ProviderAuthorization providerAuth;

    /**
     * Constructor con inyección de puertos (no depende de implementaciones concretas).
     */
    public PaymentService(PaymentRepository paymentRepository,
                          PaymentProvider paymentProvider,
                          ProviderAuthorization providerAuth) {
        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
        this.providerAuth = providerAuth;
    }

    /**
     * Caso de uso: Crear y autorizar un pago.
     * 
     * Flujo:
     * 1. Verifica si el pago ya existe (idempotencia)
     * 2. Crea nuevo agregado Payment
     * 3. Autoriza con el proveedor
     * 4. Persiste el estado
     * 
     * @param request Solicitud con amount, customerEmail, cardToken, idempotencyKey
     * @return PaymentCreatedResponse con ID y estado
     * @throws IllegalArgumentException si datos son inválidos
     * @throws PaymentProviderException si el proveedor rechaza
     */
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) 
            throws PaymentProviderException, IllegalArgumentException {
        
        // ========== Validación de entrada ==========
        if (request == null) {
            throw new IllegalArgumentException("Request no puede ser nulo");
        }
        validateCreatePaymentRequest(request);

        // ========== Idempotencia: Si existe, retorna resultado anterior ==========
        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingPayment.isPresent()) {
            Payment payment = existingPayment.get();
            return new CreatePaymentResponse(
                payment.getId().getId(),
                payment.getStatus().name(),
                payment.getProviderTransactionId()
            );
        }

        // ========== Crear agregado de dominio ==========
        Payment payment = Payment.builder()
            .amount(new Money(request.getAmount(), request.getCurrency()))
            .customerEmail(request.getCustomerEmail())
            .paymentProvider(paymentProvider.getProviderName())
            .idempotencyKey(request.getIdempotencyKey())
            .build();

        // ========== Autorizar con proveedor ==========
        PaymentAuthorizationRequest authRequest = PaymentAuthorizationRequest.builder()
            .paymentId(payment.getId().getId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .customerEmail(request.getCustomerEmail())
            .cardToken(request.getCardToken())
            .idempotencyKey(request.getIdempotencyKey())
            .build();

        PaymentAuthorizationResponse authResponse = paymentProvider.authorize(providerAuth, authRequest);

        if (authResponse.isSuccess()) {
            // Autorización exitosa: transición de estado en el agregado
            payment.authorize(authResponse.getProviderTransactionId());
        } else {
            // Proveedor rechazó: marcar como declinado
            payment.decline(authResponse.getErrorMessage());
        }

        // ========== Persistir estado ==========
        paymentRepository.save(payment);

        // ========== Retornar respuesta ==========
        return new CreatePaymentResponse(
            payment.getId().getId(),
            payment.getStatus().name(),
            payment.getProviderTransactionId()
        );
    }

    /**
     * Caso de uso: Capturar un pago autorizado.
     */
    public CapturePaymentResponse capturePayment(CapturePaymentRequest request) 
            throws PaymentProviderException {
        
        if (request == null || request.getPaymentId() == null || request.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("paymentId es requerido");
        }

        // Recuperar pago autorizado
        Optional<Payment> paymentOpt = paymentRepository.findById(new PaymentId(request.getPaymentId()));
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Pago no encontrado: " + request.getPaymentId());
        }

        Payment payment = paymentOpt.get();

        // Intentar captura en proveedor
        var captureResponse = paymentProvider.capture(providerAuth, payment.getProviderTransactionId());

        if (captureResponse.isSuccess()) {
            payment.capture();
            payment.complete();
        } else {
            payment.fail("Captura rechazada por proveedor: " + captureResponse.getErrorMessage());
        }

        paymentRepository.save(payment);

        return new CapturePaymentResponse(
            payment.getId().getId(),
            payment.getStatus().name()
        );
    }

    /**
     * Caso de uso: Reembolsar un pago completado.
     */
    public RefundPaymentResponse refundPayment(RefundPaymentRequest request) 
            throws PaymentProviderException {
        
        if (request == null || request.getPaymentId() == null || request.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("paymentId es requerido");
        }

        Optional<Payment> paymentOpt = paymentRepository.findById(new PaymentId(request.getPaymentId()));
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Pago no encontrado: " + request.getPaymentId());
        }

        Payment payment = paymentOpt.get();

        // Solicitar reembolso al proveedor
        var refundResponse = paymentProvider.refund(providerAuth, payment.getProviderTransactionId());

        if (refundResponse.isSuccess()) {
            payment.refund();
        } else {
            payment.fail("Reembolso rechazado: " + refundResponse.getErrorMessage());
        }

        paymentRepository.save(payment);

        return new RefundPaymentResponse(
            payment.getId().getId(),
            payment.getStatus().name(),
            refundResponse.getRefundId()
        );
    }

    /**
     * Consultar estado de un pago.
     */
    public GetPaymentStatusResponse getPaymentStatus(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId es requerido");
        }

        Optional<Payment> paymentOpt = paymentRepository.findById(new PaymentId(paymentId));
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Pago no encontrado: " + paymentId);
        }

        Payment payment = paymentOpt.get();
        return new GetPaymentStatusResponse(
            payment.getId().getId(),
            payment.getStatus().name(),
            payment.getAmount().toString(),
            payment.getProviderTransactionId()
        );
    }

    // ========== Validación interna ==========

    private void validateCreatePaymentRequest(CreatePaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount debe ser mayor a 0");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("currency es requerida");
        }
        if (request.getCustomerEmail() == null || request.getCustomerEmail().isBlank()) {
            throw new IllegalArgumentException("customerEmail es requerido");
        }
        if (request.getCardToken() == null || request.getCardToken().isBlank()) {
            throw new IllegalArgumentException("cardToken es requerido");
        }
        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank()) {
            throw new IllegalArgumentException("idempotencyKey es requerido (previene duplicados)");
        }
    }
}

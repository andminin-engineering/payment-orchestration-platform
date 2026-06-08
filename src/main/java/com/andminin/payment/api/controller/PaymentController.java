package com.andminin.payment.api.controller;

import com.andminin.payment.application.service.CapturePaymentRequest;
import com.andminin.payment.application.service.CapturePaymentResponse;
import com.andminin.payment.application.service.CreatePaymentRequest;
import com.andminin.payment.application.service.CreatePaymentResponse;
import com.andminin.payment.application.service.GetPaymentStatusResponse;
import com.andminin.payment.application.service.PaymentService;
import com.andminin.payment.application.service.RefundPaymentRequest;
import com.andminin.payment.application.service.RefundPaymentResponse;
import com.andminin.payment.domain.port.PaymentProviderException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller: PaymentController
 * 
 * Expone endpoints HTTP para gestionar pagos.
 * 
 * Endpoints:
 * - POST   /payments              → Crear y autorizar pago
 * - POST   /payments/{id}/capture → Capturar pago autorizado
 * - POST   /payments/{id}/refund  → Reembolsar pago
 * - GET    /payments/{id}         → Consultar estado del pago
 * 
 * Contexto: ADR-001 (Hexagonal)
 * - El controller es un ADAPTADOR de entrada (ingress adapter)
 * - Transforma HTTP requests a casos de uso de la aplicación
 * - No contiene lógica de negocio (esa está en Payment aggregate + PaymentService)
 */
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /v1/payments
     * 
     * Crea un nuevo pago y lo autoriza inmediatamente con el proveedor.
     * 
     * Idempotencia:
     * - Si el cliente reintentar con el mismo idempotencyKey,
     *   retorna el resultado anterior en lugar de crear un nuevo pago.
     * 
     * @param paymentRequest DTO con: amount, currency, customerEmail, cardToken, idempotencyKey
     * @return 201 Created con paymentId, status, providerTransactionId
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest paymentRequest)
            throws PaymentProviderException {
        CreatePaymentResponse response = paymentService.createPayment(paymentRequest);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * POST /v1/payments/{paymentId}/capture
     * 
     * Captura un pago que fue autorizado anteriormente.
     * Algunos proveedores (Stripe) requieren captura explícita.
     * 
     * @param paymentId UUID del pago a capturar
     * @return 200 OK con estado actualizado
     */
    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<CapturePaymentResponse> capturePayment(@PathVariable String paymentId)
            throws PaymentProviderException {
        CapturePaymentRequest request = new CapturePaymentRequest(paymentId);
        CapturePaymentResponse response = paymentService.capturePayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /v1/payments/{paymentId}/refund
     * 
     * Reembolsa un pago completado.
     * 
     * @param paymentId UUID del pago a reembolsar
     * @return 200 OK con refund ID
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<RefundPaymentResponse> refundPayment(@PathVariable String paymentId)
            throws PaymentProviderException {
        RefundPaymentRequest request = new RefundPaymentRequest(paymentId);
        RefundPaymentResponse response = paymentService.refundPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /v1/payments/{paymentId}
     * 
     * Consulta el estado actual de un pago.
     * 
     * @param paymentId UUID del pago
     * @return 200 OK con: paymentId, status, amount, providerTransactionId
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<GetPaymentStatusResponse> getPaymentStatus(@PathVariable String paymentId) {
        GetPaymentStatusResponse response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }
}

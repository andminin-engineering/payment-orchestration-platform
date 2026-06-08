package com.andminin.payment.config;

import com.andminin.payment.application.service.PaymentService;
import com.andminin.payment.domain.port.PaymentProvider;
import com.andminin.payment.domain.port.PaymentRepository;
import com.andminin.payment.domain.port.ProviderAuthorization;
import com.andminin.payment.infrastructure.adapter.DummyPaymentProviderAdapter;
import com.andminin.payment.infrastructure.persistence.PaymentRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring: ApplicationConfig
 * 
 * Inyección de dependencias: conecta puertos (contratos del dominio)
 * con adaptadores (implementaciones en infraestructura).
 * 
 * Contexto: ADR-001 (Hexagonal Architecture)
 * - Dominio define puertos (PaymentRepository, PaymentProvider)
 * - Infraestructura implementa adaptadores
 * - Spring los conecta en tiempo de ejecución
 * - Si necesitas cambiar adaptador, solo cambias aquí, no en el código de negocio
 */
@Configuration
public class ApplicationConfig {

    /**
     * Bean: PaymentService
     * 
     * Caso de uso orquestador. Recibe puertos inyectados.
     */
    @Bean
    public PaymentService paymentService(
            PaymentRepository paymentRepository,
            PaymentProvider paymentProvider,
            ProviderAuthorization providerAuthorization) {
        return new PaymentService(paymentRepository, paymentProvider, providerAuthorization);
    }

    /**
     * Bean: ProviderAuthorization
     * 
     * Credenciales del proveedor de pago.
     * En producción, leería desde environment variables o secrets manager.
     */
    @Bean
    public ProviderAuthorization providerAuthorization() {
        return ProviderAuthorization.builder()
            .providerName("DUMMY")
            .apiKey("dummy_api_key_for_development")
            .clientId("dummy_client_id")
            .clientSecret("dummy_client_secret")
            .testMode(true)
            .build();
    }

    /**
     * Bean: PaymentProvider
     * 
     * Para este ejercicio, usamos el adaptador Dummy.
     * En producción, cambiarías a StripeAdapter, MercadoPagoAdapter, etc.
     */
    @Bean
    public PaymentProvider paymentProvider() {
        return new DummyPaymentProviderAdapter();
    }

    /**
     * Bean: PaymentRepository
     * 
     * Implementación con Spring Data JPA + PostgreSQL.
     */
    @Bean
    public PaymentRepository paymentRepository(PaymentRepositoryAdapter adapter) {
        return adapter;
    }
}

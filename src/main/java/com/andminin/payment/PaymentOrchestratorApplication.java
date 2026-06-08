package com.andminin.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Payment Orchestration Platform.
 * 
 * Arquitectura: Hexagonal (Puertos y Adaptadores)
 * - Domain: Lógica de negocio aislada de frameworks
 * - Application: Casos de uso y orquestación
 * - Infrastructure: Adaptadores a bases de datos, APIs externas, etc.
 * - API: Puntos de entrada REST
 */
@SpringBootApplication
public class PaymentOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentOrchestratorApplication.class, args);
    }
}

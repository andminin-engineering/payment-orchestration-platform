package com.andminin.payment.domain.port;

/**
 * DTO: ProviderAuthorization
 * 
 * Credenciales específicas de cada proveedor.
 * Ej: API key de Stripe, cliente ID de MercadoPago, etc.
 */
public class ProviderAuthorization {
    private final String providerName;
    private final String apiKey;
    private final String clientId;
    private final String clientSecret;
    private final boolean testMode;

    public ProviderAuthorization(Builder builder) {
        this.providerName = builder.providerName;
        this.apiKey = builder.apiKey;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.testMode = builder.testMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getProviderName() { return providerName; }
    public String getApiKey() { return apiKey; }
    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public boolean isTestMode() { return testMode; }

    public static class Builder {
        private String providerName;
        private String apiKey;
        private String clientId;
        private String clientSecret;
        private boolean testMode = false;

        public Builder providerName(String providerName) { this.providerName = providerName; return this; }
        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder clientId(String clientId) { this.clientId = clientId; return this; }
        public Builder clientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }
        public Builder testMode(boolean testMode) { this.testMode = testMode; return this; }

        public ProviderAuthorization build() {
            return new ProviderAuthorization(this);
        }
    }
}

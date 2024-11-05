package com.ejemplo.registro.model;

public class MercadoLibreUserRequest {
    private String authorizationCode;

    // Constructor por defecto
    public MercadoLibreUserRequest() {
    }

    // Constructor con argumentos
    public MercadoLibreUserRequest(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    // Getters y setters
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
}

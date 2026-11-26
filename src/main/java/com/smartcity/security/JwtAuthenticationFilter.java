package com.smartcity.security;

/**
 * JwtAuthenticationFilter - generated from the UML diagram by diagram2app.
 */
public class JwtAuthenticationFilter {

    private JwtService jwtService;

    public JwtService getJwtService() {
        return jwtService;
    }

    public void setJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public void authenticateRequest() {
        // TODO(diagram2app): the behaviour of JwtAuthenticationFilter.authenticateRequest() is not specified in the diagram.
        throw new UnsupportedOperationException("JwtAuthenticationFilter.authenticateRequest() is not implemented: behaviour not specified");
    }
}

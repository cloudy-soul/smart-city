package com.smartcity.config;

import com.smartcity.security.AccessControlService;
import com.smartcity.security.JwtAuthenticationFilter;
import com.smartcity.security.RateLimitFilter;

/**
 * SecurityConfig - generated from the UML diagram by diagram2app.
 */
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private RateLimitFilter rateLimitFilter;
    private CorsConfig corsConfig;
    private SecurityHeadersConfig securityHeadersConfig;
    private AccessControlService accessControlService;

    public JwtAuthenticationFilter getJwtAuthenticationFilter() {
        return jwtAuthenticationFilter;
    }

    public void setJwtAuthenticationFilter(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    public RateLimitFilter getRateLimitFilter() {
        return rateLimitFilter;
    }

    public void setRateLimitFilter(RateLimitFilter rateLimitFilter) {
        this.rateLimitFilter = rateLimitFilter;
    }

    public CorsConfig getCorsConfig() {
        return corsConfig;
    }

    public void setCorsConfig(CorsConfig corsConfig) {
        this.corsConfig = corsConfig;
    }

    public SecurityHeadersConfig getSecurityHeadersConfig() {
        return securityHeadersConfig;
    }

    public void setSecurityHeadersConfig(SecurityHeadersConfig securityHeadersConfig) {
        this.securityHeadersConfig = securityHeadersConfig;
    }

    public AccessControlService getAccessControlService() {
        return accessControlService;
    }

    public void setAccessControlService(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    public void configureAuthentication() {
        // TODO(diagram2app): the behaviour of SecurityConfig.configureAuthentication() is not specified in the diagram.
        throw new UnsupportedOperationException("SecurityConfig.configureAuthentication() is not implemented: behaviour not specified");
    }

    public void configureAuthorization() {
        // TODO(diagram2app): the behaviour of SecurityConfig.configureAuthorization() is not specified in the diagram.
        throw new UnsupportedOperationException("SecurityConfig.configureAuthorization() is not implemented: behaviour not specified");
    }
}

package com.smartcity.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SecurityHeadersConfigTest {

    private static SecurityHeadersConfig sample() {
        return new SecurityHeadersConfig();
    }

    @Test
    void constructsValidInstance() {
        SecurityHeadersConfig o = sample();
        assertNotNull(o);
    }

    @Disabled("SecurityHeadersConfig.configureSecurityHeaders() behaviour is not specified in the diagram")
    @Test
    void configureSecurityHeaders0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

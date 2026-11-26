package com.smartcity.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SecurityConfigTest {

    private static SecurityConfig sample() {
        return new SecurityConfig();
    }

    @Test
    void constructsValidInstance() {
        SecurityConfig o = sample();
        assertNotNull(o);
    }

    @Disabled("SecurityConfig.configureAuthentication() behaviour is not specified in the diagram")
    @Test
    void configureAuthentication0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("SecurityConfig.configureAuthorization() behaviour is not specified in the diagram")
    @Test
    void configureAuthorization0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

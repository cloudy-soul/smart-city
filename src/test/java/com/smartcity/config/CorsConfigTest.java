package com.smartcity.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CorsConfigTest {

    private static CorsConfig sample() {
        return new CorsConfig();
    }

    @Test
    void constructsValidInstance() {
        CorsConfig o = sample();
        assertNotNull(o);
    }

    @Disabled("CorsConfig.configureAllowedOrigins() behaviour is not specified in the diagram")
    @Test
    void configureAllowedOrigins0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

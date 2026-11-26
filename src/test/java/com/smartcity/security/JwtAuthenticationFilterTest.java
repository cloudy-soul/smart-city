package com.smartcity.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JwtAuthenticationFilterTest {

    private static JwtAuthenticationFilter sample() {
        return new JwtAuthenticationFilter();
    }

    @Test
    void constructsValidInstance() {
        JwtAuthenticationFilter o = sample();
        assertNotNull(o);
    }

    @Disabled("JwtAuthenticationFilter.authenticateRequest() behaviour is not specified in the diagram")
    @Test
    void authenticateRequest0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

package com.smartcity.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static JwtService sample() {
        return new JwtService();
    }

    @Test
    void constructsValidInstance() {
        JwtService o = sample();
        assertNotNull(o);
    }

    @Disabled("JwtService.generateToken() behaviour is not specified in the diagram")
    @Test
    void generateToken0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("JwtService.validateToken() behaviour is not specified in the diagram")
    @Test
    void validateToken0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("JwtService.extractUser() behaviour is not specified in the diagram")
    @Test
    void extractUser0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

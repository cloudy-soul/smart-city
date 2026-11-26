package com.smartcity.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PasswordEncoderTest {

    private static PasswordEncoder sample() {
        return new PasswordEncoder();
    }

    @Test
    void constructsValidInstance() {
        PasswordEncoder o = sample();
        assertNotNull(o);
    }

    @Disabled("PasswordEncoder.encode() behaviour is not specified in the diagram")
    @Test
    void encode1_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("PasswordEncoder.matches() behaviour is not specified in the diagram")
    @Test
    void matches2_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

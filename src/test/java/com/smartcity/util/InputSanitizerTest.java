package com.smartcity.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class InputSanitizerTest {

    private static InputSanitizer sample() {
        return new InputSanitizer();
    }

    @Test
    void constructsValidInstance() {
        InputSanitizer o = sample();
        assertNotNull(o);
    }

    @Disabled("InputSanitizer.validateInput() behaviour is not specified in the diagram")
    @Test
    void validateInput0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("InputSanitizer.sanitizeInput() behaviour is not specified in the diagram")
    @Test
    void sanitizeInput0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

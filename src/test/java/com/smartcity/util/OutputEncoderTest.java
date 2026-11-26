package com.smartcity.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class OutputEncoderTest {

    private static OutputEncoder sample() {
        return new OutputEncoder();
    }

    @Test
    void constructsValidInstance() {
        OutputEncoder o = sample();
        assertNotNull(o);
    }

    @Disabled("OutputEncoder.encodeOutput() behaviour is not specified in the diagram")
    @Test
    void encodeOutput0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

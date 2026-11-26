package com.smartcity.media;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class Interface1Test {

    private static Interface1 sample() {
        return new Interface1() {
            @Override
            public String generateLink() {
                return "https://example.com/file.pdf";
            }
        };
    }

    @Test
    void constructsValidInstance() {
        Interface1 o = sample();
        assertNotNull(o);
    }

    @Test
    void generateLinkReturnsExpectedValue() {
        Interface1 o = sample();

        assertEquals(
            "https://example.com/file.pdf",
            o.generateLink()
        );
    }
}

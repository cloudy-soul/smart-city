package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TokenTest {

    private static Token sample() {
        return new Token(null, null, "sample");
    }

    @Test
    void constructsValidInstance() {
        Token o = sample();
        assertNotNull(o);
    }

    @Test
    void tokenValueIsStoredByConstructor() {
        Token o = sample();
        assertEquals("sample", o.getTokenValue());
    }

    @Test
    void setTokenValueUpdatesValue() {
        Token o = sample();
        o.setTokenValue("other");
        assertEquals("other", o.getTokenValue());
    }

    @Disabled("Token.isExpired() behaviour is not specified in the diagram")
    @Test
    void isExpired0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

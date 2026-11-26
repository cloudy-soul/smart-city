package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class OnlineSessionTest {

    private static OnlineSession sample() {
        return new OnlineSession(new Cookies(1L, "sample", "sample", "sample", "sample", 1.0, 1.0, "sample", "sample"), null, true, new Token(null, null, "sample"));
    }

    @Test
    void constructsValidInstance() {
        OnlineSession o = sample();
        assertNotNull(o);
    }

    @Test
    void authenticatedIsStoredByConstructor() {
        OnlineSession o = sample();
        assertEquals(true, o.isAuthenticated());
    }

    @Test
    void setAuthenticatedUpdatesValue() {
        OnlineSession o = sample();
        o.setAuthenticated(false);
        assertEquals(false, o.isAuthenticated());
    }

    @Disabled("OnlineSession.startSession() behaviour is not specified in the diagram")
    @Test
    void startSession0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("OnlineSession.endSession() behaviour is not specified in the diagram")
    @Test
    void endSession0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("OnlineSession.validateSession() behaviour is not specified in the diagram")
    @Test
    void validateSession0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

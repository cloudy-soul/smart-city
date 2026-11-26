package com.smartcity.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private static AuthService sample() {
        return new AuthService();
    }

    @Test
    void constructsValidInstance() {
        AuthService o = sample();
        assertNotNull(o);
    }

    @Disabled("AuthService.authenticate() behaviour is not specified in the diagram")
    @Test
    void authenticate0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("AuthService.register() behaviour is not specified in the diagram")
    @Test
    void register0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("AuthService.resetPassword() behaviour is not specified in the diagram")
    @Test
    void resetPassword0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("AuthService.logout() behaviour is not specified in the diagram")
    @Test
    void logout0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

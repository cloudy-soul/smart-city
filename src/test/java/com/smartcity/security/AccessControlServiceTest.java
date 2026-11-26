package com.smartcity.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AccessControlServiceTest {

    private static AccessControlService sample() {
        return new AccessControlService();
    }

    @Test
    void constructsValidInstance() {
        AccessControlService o = sample();
        assertNotNull(o);
    }

    @Disabled("AccessControlService.checkPermission() behaviour is not specified in the diagram")
    @Test
    void checkPermission0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("AccessControlService.checkRole() behaviour is not specified in the diagram")
    @Test
    void checkRole0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("AccessControlService.checkOwnership() behaviour is not specified in the diagram")
    @Test
    void checkOwnership0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

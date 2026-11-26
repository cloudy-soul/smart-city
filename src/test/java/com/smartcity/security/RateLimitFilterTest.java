package com.smartcity.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RateLimitFilterTest {

    private static RateLimitFilter sample() {
        return new RateLimitFilter();
    }

    @Test
    void constructsValidInstance() {
        RateLimitFilter o = sample();
        assertNotNull(o);
    }

    @Disabled("RateLimitFilter.checkRequestLimit() behaviour is not specified in the diagram")
    @Test
    void checkRequestLimit0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("RateLimitFilter.blockExcessRequests() behaviour is not specified in the diagram")
    @Test
    void blockExcessRequests0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

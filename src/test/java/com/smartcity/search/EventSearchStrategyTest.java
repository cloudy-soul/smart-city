package com.smartcity.search;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EventSearchStrategyTest {

    private static EventSearchStrategy sample() {
        return new EventSearchStrategy();
    }

    @Test
    void constructsValidInstance() {
        EventSearchStrategy o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof SearchStrategy);
    }

    @Disabled("EventSearchStrategy.search() behaviour is not specified in the diagram")
    @Test
    void search1_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

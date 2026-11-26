package com.smartcity.factory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PlaceFactoryTest {

    private static PlaceFactory sample() {
        return new PlaceFactory();
    }

    @Test
    void constructsValidInstance() {
        PlaceFactory o = sample();
        assertNotNull(o);
    }

    @Disabled("PlaceFactory.createPlace() behaviour is not specified in the diagram")
    @Test
    void createPlace1_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

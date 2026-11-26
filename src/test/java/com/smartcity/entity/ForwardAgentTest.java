package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ForwardAgentTest {

    private static ForwardAgent sample() {
        return new ForwardAgent("sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        ForwardAgent o = sample();
        assertNotNull(o);
    }

    @Test
    void destinationIsStoredByConstructor() {
        ForwardAgent o = sample();
        assertEquals("sample", o.getDestination());
    }

    @Test
    void forwardingTypeIsStoredByConstructor() {
        ForwardAgent o = sample();
        assertEquals("sample", o.getForwardingType());
    }

    @Test
    void setDestinationUpdatesValue() {
        ForwardAgent o = sample();
        o.setDestination("other");
        assertEquals("other", o.getDestination());
    }

    @Test
    void setForwardingTypeUpdatesValue() {
        ForwardAgent o = sample();
        o.setForwardingType("other");
        assertEquals("other", o.getForwardingType());
    }

    @Disabled("ForwardAgent.generateLink() behaviour is not specified in the diagram")
    @Test
    void generateLink0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("ForwardAgent.forward() behaviour is not specified in the diagram")
    @Test
    void forward0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

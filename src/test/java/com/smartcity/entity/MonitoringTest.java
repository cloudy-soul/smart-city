package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class MonitoringTest {

    private static Monitoring sample() {
        return new Monitoring("sample", "sample", "sample", new HashMap<>(), null);
    }

    @Test
    void constructsValidInstance() {
        Monitoring o = sample();
        assertNotNull(o);
    }

    @Test
    void recordTypeIsStoredByConstructor() {
        Monitoring o = sample();
        assertEquals("sample", o.getRecordType());
    }

    @Test
    void sourceIsStoredByConstructor() {
        Monitoring o = sample();
        assertEquals("sample", o.getSource());
    }

    @Test
    void statusIsStoredByConstructor() {
        Monitoring o = sample();
        assertEquals("sample", o.getStatus());
    }

    @Test
    void dataIsStoredByConstructor() {
        Monitoring o = sample();
        assertEquals(new HashMap<>(), o.getData());
    }

    @Test
    void setRecordTypeUpdatesValue() {
        Monitoring o = sample();
        o.setRecordType("other");
        assertEquals("other", o.getRecordType());
    }

    @Test
    void setSourceUpdatesValue() {
        Monitoring o = sample();
        o.setSource("other");
        assertEquals("other", o.getSource());
    }

    @Test
    void setStatusUpdatesValue() {
        Monitoring o = sample();
        o.setStatus("other");
        assertEquals("other", o.getStatus());
    }

    @Test
    void setDataUpdatesValue() {
        Monitoring o = sample();
        o.setData(new HashMap<>());
        assertEquals(new HashMap<>(), o.getData());
    }

    @Disabled("Monitoring.recordEvent() behaviour is not specified in the diagram")
    @Test
    void recordEvent0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("Monitoring.retrieveRecords() behaviour is not specified in the diagram")
    @Test
    void retrieveRecords0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

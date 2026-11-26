package com.smartcity.media;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class mediaAgentTest {

    private static mediaAgent sample() {
        return new mediaAgent("sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        mediaAgent o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof Interface1);
    }

    @Test
    void fileNameIsStoredByConstructor() {
        mediaAgent o = sample();
        assertEquals("sample", o.getFileName());
    }

    @Test
    void fileTypeIsStoredByConstructor() {
        mediaAgent o = sample();
        assertEquals("sample", o.getFileType());
    }

    @Test
    void setFileNameUpdatesValue() {
        mediaAgent o = sample();
        o.setFileName("other");
        assertEquals("other", o.getFileName());
    }

    @Test
    void setFileTypeUpdatesValue() {
        mediaAgent o = sample();
        o.setFileType("other");
        assertEquals("other", o.getFileType());
    }

    @Disabled("mediaAgent.generateLink() behaviour is not specified in the diagram")
    @Test
    void generateLink0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("mediaAgent.generatePath() behaviour is not specified in the diagram")
    @Test
    void generatePath0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

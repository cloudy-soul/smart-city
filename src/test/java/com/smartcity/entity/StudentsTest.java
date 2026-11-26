package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class StudentsTest {

    private static Students sample() {
        return new Students(1L, new CV("sample", "sample", null, "sample", "sample", "sample", "sample", "sample"));
    }

    @Test
    void constructsValidInstance() {
        Students o = sample();
        assertNotNull(o);
    }

    @Test
    void studentIDIsStoredByConstructor() {
        Students o = sample();
        assertEquals(1L, o.getStudentID());
    }

    @Test
    void setStudentIDUpdatesValue() {
        Students o = sample();
        o.setStudentID(2L);
        assertEquals(2L, o.getStudentID());
    }

    @Disabled("Students.updateStudentProfile() behaviour is not specified in the diagram")
    @Test
    void updateStudentProfile0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

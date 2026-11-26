package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AdminTest {

    private static Admin sample() {
        return new Admin(1, 1L);
    }

    @Test
    void constructsValidInstance() {
        Admin o = sample();
        assertNotNull(o);
    }

    @Test
    void authorization_levelIsStoredByConstructor() {
        Admin o = sample();
        assertEquals(1, o.getAuthorization_level());
    }

    @Test
    void adminIDIsStoredByConstructor() {
        Admin o = sample();
        assertEquals(1L, o.getAdminID());
    }

    @Test
    void setAuthorization_levelUpdatesValue() {
        Admin o = sample();
        o.setAuthorization_level(2);
        assertEquals(2, o.getAuthorization_level());
    }

    @Test
    void setAdminIDUpdatesValue() {
        Admin o = sample();
        o.setAdminID(2L);
        assertEquals(2L, o.getAdminID());
    }
}

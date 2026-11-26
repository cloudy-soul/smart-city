package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TouristsTest {

    private static Tourists sample() {
        return new Tourists(1L, "sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        Tourists o = sample();
        assertNotNull(o);
    }

    @Test
    void touristIDIsStoredByConstructor() {
        Tourists o = sample();
        assertEquals(1L, o.getTouristID());
    }

    @Test
    void preferredLanguageIsStoredByConstructor() {
        Tourists o = sample();
        assertEquals("sample", o.getPreferredLanguage());
    }

    @Test
    void travelPurposeIsStoredByConstructor() {
        Tourists o = sample();
        assertEquals("sample", o.getTravelPurpose());
    }

    @Test
    void setTouristIDUpdatesValue() {
        Tourists o = sample();
        o.setTouristID(2L);
        assertEquals(2L, o.getTouristID());
    }

    @Test
    void setPreferredLanguageUpdatesValue() {
        Tourists o = sample();
        o.setPreferredLanguage("other");
        assertEquals("other", o.getPreferredLanguage());
    }

    @Test
    void setTravelPurposeUpdatesValue() {
        Tourists o = sample();
        o.setTravelPurpose("other");
        assertEquals("other", o.getTravelPurpose());
    }

    @Test
    void touristIDRejects_not_null() {
        Tourists o = sample();
        o.setTouristID(null);
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

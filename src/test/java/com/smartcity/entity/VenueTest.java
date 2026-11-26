package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class VenueTest {

    private static Venue sample() {
        return new Venue("sample", "sample", 1.0, 1.0, "sample", new City("sample", "sample", "sample", 1.0, 1.0), "sample", 1);
    }

    @Test
    void constructsValidInstance() {
        Venue o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof Place);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void nameIsStoredByConstructor() {
        Venue o = sample();
        assertEquals("sample", o.getName());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        Venue o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void latitudeIsStoredByConstructor() {
        Venue o = sample();
        assertEquals(1.0, o.getLatitude(), 1e-9);
    }

    @Test
    void longitudeIsStoredByConstructor() {
        Venue o = sample();
        assertEquals(1.0, o.getLongitude(), 1e-9);
    }

    @Test
    void addressIsStoredByConstructor() {
        Venue o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void venueTypeIsStoredByConstructor() {
        Venue o = sample();
        assertEquals("sample", o.getVenueType());
    }

    @Test
    void capacityIsStoredByConstructor() {
        Venue o = sample();
        assertEquals(1, o.getCapacity());
    }

    @Test
    void setNameUpdatesValue() {
        Venue o = sample();
        o.setName("other");
        assertEquals("other", o.getName());
    }

    @Test
    void setDescriptionUpdatesValue() {
        Venue o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setLatitudeUpdatesValue() {
        Venue o = sample();
        o.setLatitude(2.0);
        assertEquals(2.0, o.getLatitude(), 1e-9);
    }

    @Test
    void setLongitudeUpdatesValue() {
        Venue o = sample();
        o.setLongitude(2.0);
        assertEquals(2.0, o.getLongitude(), 1e-9);
    }

    @Test
    void setAddressUpdatesValue() {
        Venue o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void setVenueTypeUpdatesValue() {
        Venue o = sample();
        o.setVenueType("other");
        assertEquals("other", o.getVenueType());
    }

    @Test
    void setCapacityUpdatesValue() {
        Venue o = sample();
        o.setCapacity(2);
        assertEquals(2, o.getCapacity());
    }

    @Test
    void nameRejects_not_blank() {
        Venue o = sample();
        o.setName("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

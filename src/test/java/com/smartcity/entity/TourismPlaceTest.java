package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TourismPlaceTest {

    private static TourismPlace sample() {
        return new TourismPlace("sample", "sample", 1.0, 1.0, "sample", new City("sample", "sample", "sample", 1.0, 1.0), "sample", "sample", 1.0);
    }

    @Test
    void constructsValidInstance() {
        TourismPlace o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof Place);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void nameIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals("sample", o.getName());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void latitudeIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals(1.0, o.getLatitude(), 1e-9);
    }

    @Test
    void longitudeIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals(1.0, o.getLongitude(), 1e-9);
    }

    @Test
    void addressIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void categoryIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals("sample", o.getCategory());
    }

    @Test
    void openingHoursIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals("sample", o.getOpeningHours());
    }

    @Test
    void ratingIsStoredByConstructor() {
        TourismPlace o = sample();
        assertEquals(1.0, o.getRating(), 1e-9);
    }

    @Test
    void setNameUpdatesValue() {
        TourismPlace o = sample();
        o.setName("other");
        assertEquals("other", o.getName());
    }

    @Test
    void setDescriptionUpdatesValue() {
        TourismPlace o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setLatitudeUpdatesValue() {
        TourismPlace o = sample();
        o.setLatitude(2.0);
        assertEquals(2.0, o.getLatitude(), 1e-9);
    }

    @Test
    void setLongitudeUpdatesValue() {
        TourismPlace o = sample();
        o.setLongitude(2.0);
        assertEquals(2.0, o.getLongitude(), 1e-9);
    }

    @Test
    void setAddressUpdatesValue() {
        TourismPlace o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void setCategoryUpdatesValue() {
        TourismPlace o = sample();
        o.setCategory("other");
        assertEquals("other", o.getCategory());
    }

    @Test
    void setOpeningHoursUpdatesValue() {
        TourismPlace o = sample();
        o.setOpeningHours("other");
        assertEquals("other", o.getOpeningHours());
    }

    @Test
    void setRatingUpdatesValue() {
        TourismPlace o = sample();
        o.setRating(2.0);
        assertEquals(2.0, o.getRating(), 1e-9);
    }

    @Test
    void nameRejects_not_blank() {
        TourismPlace o = sample();
        o.setName("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

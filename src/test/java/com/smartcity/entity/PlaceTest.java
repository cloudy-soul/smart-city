package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PlaceTest {

    private static Place sample() {
    return new Place(
        "sample",
        "sample",
        1.0,
        1.0,
        "sample",
        new City("sample", "sample", "sample", 1.0, 1.0)
    ) {};
}

    @Test
    void constructsValidInstance() {
        Place o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void nameIsStoredByConstructor() {
        Place o = sample();
        assertEquals("sample", o.getName());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        Place o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void latitudeIsStoredByConstructor() {
        Place o = sample();
        assertEquals(1.0, o.getLatitude(), 1e-9);
    }

    @Test
    void longitudeIsStoredByConstructor() {
        Place o = sample();
        assertEquals(1.0, o.getLongitude(), 1e-9);
    }

    @Test
    void addressIsStoredByConstructor() {
        Place o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void setNameUpdatesValue() {
        Place o = sample();
        o.setName("other");
        assertEquals("other", o.getName());
    }

    @Test
    void setDescriptionUpdatesValue() {
        Place o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setLatitudeUpdatesValue() {
        Place o = sample();
        o.setLatitude(2.0);
        assertEquals(2.0, o.getLatitude(), 1e-9);
    }

    @Test
    void setLongitudeUpdatesValue() {
        Place o = sample();
        o.setLongitude(2.0);
        assertEquals(2.0, o.getLongitude(), 1e-9);
    }

    @Test
    void setAddressUpdatesValue() {
        Place o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void nameRejects_not_blank() {
        Place o = sample();
        o.setName("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Disabled("Place.getName() behaviour is not specified in the diagram")
    @Test
    void getName1_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("Place.getCoordinates() behaviour is not specified in the diagram")
    @Test
    void getCoordinates0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}

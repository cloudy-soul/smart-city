package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EducationalInstitutionTest {

    private static EducationalInstitution sample() {
        return new EducationalInstitution("sample", "sample", 1.0, 1.0, "sample", new City("sample", "sample", "sample", 1.0, 1.0), "sample", "sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        EducationalInstitution o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof Place);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void nameIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getName());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void latitudeIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals(1.0, o.getLatitude(), 1e-9);
    }

    @Test
    void longitudeIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals(1.0, o.getLongitude(), 1e-9);
    }

    @Test
    void addressIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void institutionTypeIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getInstitutionType());
    }

    @Test
    void websiteIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getWebsite());
    }

    @Test
    void contactEmailIsStoredByConstructor() {
        EducationalInstitution o = sample();
        assertEquals("sample", o.getContactEmail());
    }

    @Test
    void setNameUpdatesValue() {
        EducationalInstitution o = sample();
        o.setName("other");
        assertEquals("other", o.getName());
    }

    @Test
    void setDescriptionUpdatesValue() {
        EducationalInstitution o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setLatitudeUpdatesValue() {
        EducationalInstitution o = sample();
        o.setLatitude(2.0);
        assertEquals(2.0, o.getLatitude(), 1e-9);
    }

    @Test
    void setLongitudeUpdatesValue() {
        EducationalInstitution o = sample();
        o.setLongitude(2.0);
        assertEquals(2.0, o.getLongitude(), 1e-9);
    }

    @Test
    void setAddressUpdatesValue() {
        EducationalInstitution o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void setInstitutionTypeUpdatesValue() {
        EducationalInstitution o = sample();
        o.setInstitutionType("other");
        assertEquals("other", o.getInstitutionType());
    }

    @Test
    void setWebsiteUpdatesValue() {
        EducationalInstitution o = sample();
        o.setWebsite("other");
        assertEquals("other", o.getWebsite());
    }

    @Test
    void setContactEmailUpdatesValue() {
        EducationalInstitution o = sample();
        o.setContactEmail("other");
        assertEquals("other", o.getContactEmail());
    }

    @Test
    void nameRejects_not_blank() {
        EducationalInstitution o = sample();
        o.setName("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void contactEmailRejects_not_blank() {
        EducationalInstitution o = sample();
        o.setContactEmail("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

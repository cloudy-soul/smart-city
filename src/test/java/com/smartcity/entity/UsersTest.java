package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class UsersTest {

    private static Users sample() {
        return new Users("sample", "sample", "user@example.com", "sample", "sample", "sample", "sample", "sample", "sample", LocalDate.of(2024, 1, 15), "sample", "sample", "sample", new Account("sample", "sample"));
    }

    @Test
    void constructsValidInstance() {
        Users o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void firstnameIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getFirstname());
    }

    @Test
    void lastnameIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getLastname());
    }

    @Test
    void emailIsStoredByConstructor() {
        Users o = sample();
        assertEquals("user@example.com", o.getEmail());
    }

    @Test
    void phoneIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getPhone());
    }

    @Test
    void countryIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getCountry());
    }

    @Test
    void cityIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getCity());
    }

    @Test
    void regionIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getRegion());
    }

    @Test
    void addressIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void nationalityIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getNationality());
    }

    @Test
    void birthdayIsStoredByConstructor() {
        Users o = sample();
        assertEquals(LocalDate.of(2024, 1, 15), o.getBirthday());
    }

    @Test
    void zipcodeIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getZipcode());
    }

    @Test
    void nationalIDIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getNationalID());
    }

    @Test
    void userCodeIsStoredByConstructor() {
        Users o = sample();
        assertEquals("sample", o.getUserCode());
    }

    @Test
    void setFirstnameUpdatesValue() {
        Users o = sample();
        o.setFirstname("other");
        assertEquals("other", o.getFirstname());
    }

    @Test
    void setLastnameUpdatesValue() {
        Users o = sample();
        o.setLastname("other");
        assertEquals("other", o.getLastname());
    }

    @Test
    void setEmailUpdatesValue() {
        Users o = sample();
        o.setEmail("other@example.com");
        assertEquals("other@example.com", o.getEmail());
    }

    @Test
    void setPhoneUpdatesValue() {
        Users o = sample();
        o.setPhone("other");
        assertEquals("other", o.getPhone());
    }

    @Test
    void setCountryUpdatesValue() {
        Users o = sample();
        o.setCountry("other");
        assertEquals("other", o.getCountry());
    }

    @Test
    void setCityUpdatesValue() {
        Users o = sample();
        o.setCity("other");
        assertEquals("other", o.getCity());
    }

    @Test
    void setRegionUpdatesValue() {
        Users o = sample();
        o.setRegion("other");
        assertEquals("other", o.getRegion());
    }

    @Test
    void setAddressUpdatesValue() {
        Users o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void setNationalityUpdatesValue() {
        Users o = sample();
        o.setNationality("other");
        assertEquals("other", o.getNationality());
    }

    @Test
    void setBirthdayUpdatesValue() {
        Users o = sample();
        o.setBirthday(LocalDate.of(2024, 2, 20));
        assertEquals(LocalDate.of(2024, 2, 20), o.getBirthday());
    }

    @Test
    void setZipcodeUpdatesValue() {
        Users o = sample();
        o.setZipcode("other");
        assertEquals("other", o.getZipcode());
    }

    @Test
    void setNationalIDUpdatesValue() {
        Users o = sample();
        o.setNationalID("other");
        assertEquals("other", o.getNationalID());
    }

    @Test
    void setUserCodeUpdatesValue() {
        Users o = sample();
        o.setUserCode("other");
        assertEquals("other", o.getUserCode());
    }

    @Test
    void emailRejects_email() {
        Users o = sample();
        o.setEmail("not-an-email");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void phoneRejects_not_null() {
        Users o = sample();
        o.setPhone(null);
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void zipcodeRejects_not_null() {
        Users o = sample();
        o.setZipcode(null);
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void userCodeRejects_not_null() {
        Users o = sample();
        o.setUserCode(null);
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

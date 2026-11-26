package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CompanyTest {

    private static Company sample() {
        return new Company("sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        Company o = sample();
        assertNotNull(o);
    }

    @Test
    void nameIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getName());
    }

    @Test
    void titleIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getTitle());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void countryIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getCountry());
    }

    @Test
    void cityIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getCity());
    }

    @Test
    void regionIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getRegion());
    }

    @Test
    void addressIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getAddress());
    }

    @Test
    void phoneIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getPhone());
    }

    @Test
    void emailIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getEmail());
    }

    @Test
    void taxIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getTax());
    }

    @Test
    void domainNameIsStoredByConstructor() {
        Company o = sample();
        assertEquals("sample", o.getDomainName());
    }

    @Test
    void setNameUpdatesValue() {
        Company o = sample();
        o.setName("other");
        assertEquals("other", o.getName());
    }

    @Test
    void setTitleUpdatesValue() {
        Company o = sample();
        o.setTitle("other");
        assertEquals("other", o.getTitle());
    }

    @Test
    void setDescriptionUpdatesValue() {
        Company o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setCountryUpdatesValue() {
        Company o = sample();
        o.setCountry("other");
        assertEquals("other", o.getCountry());
    }

    @Test
    void setCityUpdatesValue() {
        Company o = sample();
        o.setCity("other");
        assertEquals("other", o.getCity());
    }

    @Test
    void setRegionUpdatesValue() {
        Company o = sample();
        o.setRegion("other");
        assertEquals("other", o.getRegion());
    }

    @Test
    void setAddressUpdatesValue() {
        Company o = sample();
        o.setAddress("other");
        assertEquals("other", o.getAddress());
    }

    @Test
    void setPhoneUpdatesValue() {
        Company o = sample();
        o.setPhone("other");
        assertEquals("other", o.getPhone());
    }

    @Test
    void setEmailUpdatesValue() {
        Company o = sample();
        o.setEmail("other");
        assertEquals("other", o.getEmail());
    }

    @Test
    void setTaxUpdatesValue() {
        Company o = sample();
        o.setTax("other");
        assertEquals("other", o.getTax());
    }

    @Test
    void setDomainNameUpdatesValue() {
        Company o = sample();
        o.setDomainName("other");
        assertEquals("other", o.getDomainName());
    }

    @Test
    void nameRejects_not_blank() {
        Company o = sample();
        o.setName("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void titleRejects_not_blank() {
        Company o = sample();
        o.setTitle("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void phoneRejects_not_blank() {
        Company o = sample();
        o.setPhone("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void emailRejects_not_blank() {
        Company o = sample();
        o.setEmail("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

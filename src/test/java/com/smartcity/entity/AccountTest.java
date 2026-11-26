package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static Account sample() {
        return new Account("sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        Account o = sample();
        assertNotNull(o);
    }

    @Test
    void usernameIsStoredByConstructor() {
        Account o = sample();
        assertEquals("sample", o.getUsername());
    }

    @Test
    void passwordHashIsStoredByConstructor() {
        Account o = sample();
        assertEquals("sample", o.getPasswordHash());
    }

    @Test
    void setUsernameUpdatesValue() {
        Account o = sample();
        o.setUsername("other");
        assertEquals("other", o.getUsername());
    }

    @Test
    void setPasswordHashUpdatesValue() {
        Account o = sample();
        o.setPasswordHash("other");
        assertEquals("other", o.getPasswordHash());
    }

    @Test
    void usernameRejects_not_blank() {
        Account o = sample();
        o.setUsername("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Test
    void passwordHashRejects_not_blank() {
        Account o = sample();
        o.setPasswordHash("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

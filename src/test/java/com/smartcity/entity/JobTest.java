package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class JobTest {

    private static Job sample() {
        return new Job("sample", "sample", "sample", "sample", 1.0);
    }

    @Test
    void constructsValidInstance() {
        Job o = sample();
        assertNotNull(o);
        assertTrue((Object) o instanceof BaseEntity);
    }

    @Test
    void titleIsStoredByConstructor() {
        Job o = sample();
        assertEquals("sample", o.getTitle());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        Job o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void industryIsStoredByConstructor() {
        Job o = sample();
        assertEquals("sample", o.getIndustry());
    }

    @Test
    void employmentTypeIsStoredByConstructor() {
        Job o = sample();
        assertEquals("sample", o.getEmploymentType());
    }

    @Test
    void salaryIsStoredByConstructor() {
        Job o = sample();
        assertEquals(1.0, o.getSalary(), 1e-9);
    }

    @Test
    void setTitleUpdatesValue() {
        Job o = sample();
        o.setTitle("other");
        assertEquals("other", o.getTitle());
    }

    @Test
    void setDescriptionUpdatesValue() {
        Job o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setIndustryUpdatesValue() {
        Job o = sample();
        o.setIndustry("other");
        assertEquals("other", o.getIndustry());
    }

    @Test
    void setEmploymentTypeUpdatesValue() {
        Job o = sample();
        o.setEmploymentType("other");
        assertEquals("other", o.getEmploymentType());
    }

    @Test
    void setSalaryUpdatesValue() {
        Job o = sample();
        o.setSalary(2.0);
        assertEquals(2.0, o.getSalary(), 1e-9);
    }

    @Test
    void titleRejects_not_blank() {
        Job o = sample();
        o.setTitle("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

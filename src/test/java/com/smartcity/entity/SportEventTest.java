package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SportEventTest {

    private static SportEvent sample() {
        return new SportEvent("sample", "sample", LocalDateTime.of(2024, 1, 15, 10, 30), 1.0, new Venue("sample", "sample", 1.0, 1.0, "sample", new City("sample", "sample", "sample", 1.0, 1.0), "sample", 1));
    }

    @Test
    void constructsValidInstance() {
        SportEvent o = sample();
        assertNotNull(o);
    }

    @Test
    void titleIsStoredByConstructor() {
        SportEvent o = sample();
        assertEquals("sample", o.getTitle());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        SportEvent o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void eventDateIsStoredByConstructor() {
        SportEvent o = sample();
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), o.getEventDate());
    }

    @Test
    void ticketPriceIsStoredByConstructor() {
        SportEvent o = sample();
        assertEquals(1.0, o.getTicketPrice(), 1e-9);
    }

    @Test
    void setTitleUpdatesValue() {
        SportEvent o = sample();
        o.setTitle("other");
        assertEquals("other", o.getTitle());
    }

    @Test
    void setDescriptionUpdatesValue() {
        SportEvent o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setEventDateUpdatesValue() {
        SportEvent o = sample();
        o.setEventDate(LocalDateTime.of(2024, 2, 20, 11, 45));
        assertEquals(LocalDateTime.of(2024, 2, 20, 11, 45), o.getEventDate());
    }

    @Test
    void setTicketPriceUpdatesValue() {
        SportEvent o = sample();
        o.setTicketPrice(2.0);
        assertEquals(2.0, o.getTicketPrice(), 1e-9);
    }

    @Test
    void titleRejects_not_blank() {
        SportEvent o = sample();
        o.setTitle("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }
}

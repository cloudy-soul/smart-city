package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/** Request/response body for Booking. References are ids. */
public record BookingDto(
        Long id,
        @NotNull LocalDateTime bookingDate,
        Long venueId) {
}

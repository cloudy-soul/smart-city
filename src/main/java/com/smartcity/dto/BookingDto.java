package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.smartcity.enums.BookingStatus;

/** Request/response body for Booking. References are ids. */
public record BookingDto(
        Long id,
        @NotNull LocalDateTime bookingDate,
        Long venueId,
        Long eventId,
        Long userId,
        int quantity,
        BigDecimal unitPrice,
        BookingStatus status) {
}

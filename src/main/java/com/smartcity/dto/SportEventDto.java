package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/** Request/response body for SportEvent. References are ids. */
public record SportEventDto(
        Long id,
        @NotBlank String title,
        @NotNull String description,
        @NotNull LocalDateTime eventDate,
        @NotNull Double ticketPrice,
        @NotNull Long venueId) {
}

package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record VenueSlotDto(Long id, @NotNull Long venueId,
                           @NotNull LocalDateTime startsAt, @NotNull LocalDateTime endsAt) {
}

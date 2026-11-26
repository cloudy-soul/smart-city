package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/** Request/response body for Token. References are ids. */
public record TokenDto(
        Long id,
        @NotNull Instant creationDate,
        @NotNull Instant expirationDateTime,
        @NotNull String tokenValue) {
}

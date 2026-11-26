package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Tourists. References are ids. */
public record TouristsDto(
        Long id,
        @NotNull Long touristID,
        @NotNull String preferredLanguage,
        @NotNull String travelPurpose) {
}

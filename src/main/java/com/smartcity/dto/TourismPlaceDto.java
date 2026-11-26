package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for TourismPlace. References are ids. */
public record TourismPlaceDto(
        Long id,
        @NotBlank String name,
        @NotNull String description,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull String address,
        @NotNull Long cityId,
        @NotNull String category,
        @NotNull String openingHours,
        @NotNull Double rating) {
}

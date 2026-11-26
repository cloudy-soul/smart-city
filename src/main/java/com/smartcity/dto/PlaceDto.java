package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Place. References are ids. */
public record PlaceDto(
        Long id,
        @NotBlank String name,
        @NotNull String description,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull String address,
        @NotNull Long cityId) {
}

package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for City. References are ids. */
public record CityDto(
        Long id,
        @NotBlank String name,
        @NotNull String country,
        @NotNull String description,
        @NotNull Double latitude,
        @NotNull Double longitude) {
}

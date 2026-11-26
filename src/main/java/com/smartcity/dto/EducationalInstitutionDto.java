package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for EducationalInstitution. References are ids. */
public record EducationalInstitutionDto(
        Long id,
        @NotBlank String name,
        @NotNull String description,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull String address,
        @NotNull Long cityId,
        @NotNull String institutionType,
        @NotNull String website,
        @NotBlank String contactEmail) {
}

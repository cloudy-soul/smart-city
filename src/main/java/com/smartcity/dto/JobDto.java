package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Job. References are ids. */
public record JobDto(
        Long id,
        @NotBlank String title,
        @NotNull String description,
        @NotNull String industry,
        @NotNull String employmentType,
        @NotNull Double salary,
        Long companyId) {
}

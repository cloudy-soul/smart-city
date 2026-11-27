package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Company. References are ids. */
public record CompanyDto(
        Long id,
        @NotBlank String name,
        @NotBlank String title,
        @NotNull String description,
        @NotNull String country,
        @NotNull String city,
        @NotNull String region,
        @NotNull String address,
        @NotBlank String phone,
        @NotBlank String email,
        @NotNull String tax,
        @NotNull String domainName,
        Double latitude,
        Double longitude) {
}

package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Admin. References are ids. */
public record AdminDto(
        Long id,
        @NotNull Integer authorization_level,
        @NotNull Long adminID) {
}

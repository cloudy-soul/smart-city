package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/** Request/response body for JobApplication. References are ids. */
public record JobApplicationDto(
        Long id,
        @NotNull Instant applicationDate,
        Long jobId,
        Long cVId) {
}

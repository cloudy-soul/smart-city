package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import com.smartcity.enums.ApplicationStatus;

/** Request/response body for JobApplication. References are ids. */
public record JobApplicationDto(
        Long id,
        @NotNull Instant applicationDate,
        Long jobId,
        Long cVId,
        Long userId,
        String coverNote,
        ApplicationStatus status,
        boolean sampleSubmission) {
}

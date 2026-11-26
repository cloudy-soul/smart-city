package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/** Request/response body for Monitoring. References are ids. */
public record MonitoringDto(
        Long id,
        @NotNull String recordType,
        @NotNull String source,
        @NotNull String status,
        @NotNull Instant timestamp) {
}

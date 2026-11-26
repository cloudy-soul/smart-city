package com.smartcity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/** Request/response body for CV. References are ids. */
public record CVDto(
        Long id,
        @NotBlank String title,
        @NotNull String description,
        @NotNull Instant lastUpdate,
        @NotNull String fileType,
        @NotNull String storageServer,
        @NotNull String storagePath,
        @NotNull String fileName,
        @NotNull String fileExtension) {
}

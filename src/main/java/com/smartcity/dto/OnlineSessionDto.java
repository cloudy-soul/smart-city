package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

/** Request/response body for OnlineSession. References are ids. */
public record OnlineSessionDto(
        Long id,
        @NotNull Instant timeStamp,
        @NotNull Boolean authenticated,
        @NotNull Long tokenId) {
}

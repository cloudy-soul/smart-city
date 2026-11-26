package com.smartcity.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Request/response body for Students. References are ids. */
public record StudentsDto(
        Long id,
        @NotNull Long studentID,
        @NotNull Long CVId,
        Long educationalInstitutionId,
        List<Long> cvsIds) {
}

package com.smartcity.dto;

import com.smartcity.entity.BusinessNews.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record BusinessNewsDto(Long id, @NotNull Long companyId, @NotBlank @Size(max = 255) String title,
                              @NotBlank @Size(max = 4000) String body, @NotNull Category category,
                              boolean published, Instant publishedAt) {
}

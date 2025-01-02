package inha.git.category.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchCategoryResponse(
        @NotNull
        @Schema(description = "카테고리 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "카테고리 이름", example = "교과")
        String name
) {
}

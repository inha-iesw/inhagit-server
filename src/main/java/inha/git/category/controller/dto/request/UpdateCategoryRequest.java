package inha.git.category.controller.dto.request;

import inha.git.common.validation.annotation.CategoryNameUnique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
        @NotNull
        @CategoryNameUnique
        @Schema(description = "카테고리 이름", example = "비교과")
        String name) {
}

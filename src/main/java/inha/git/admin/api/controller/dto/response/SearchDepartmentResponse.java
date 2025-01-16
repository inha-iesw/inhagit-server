package inha.git.admin.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchDepartmentResponse(
        @NotNull
        @Schema(description = "학과 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "학과 이름", example = "컴퓨터공학과")
        String name
) {
}

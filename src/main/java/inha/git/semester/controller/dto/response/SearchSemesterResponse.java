package inha.git.semester.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchSemesterResponse(

        @NotNull
        @Schema(description = "학기 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "학기 이름", example = "24-1학기")
        String name
) {
}

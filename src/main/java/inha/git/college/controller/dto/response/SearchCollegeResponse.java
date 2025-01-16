package inha.git.college.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchCollegeResponse(

        @NotNull
        @Schema(description = "단과대 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "단과대 이름", example = "소프트웨어융합대학")
        String name
) {
}

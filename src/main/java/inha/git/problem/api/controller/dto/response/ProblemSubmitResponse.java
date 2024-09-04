package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProblemSubmitResponse(

        @NotNull
        @Schema(description = "제출한 문제의 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "제출한 문제 파일 경로",example = "/problem-zip/1725415249781-180393.zip")
        String zipFilePath
) {
}

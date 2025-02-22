package inha.git.problem.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProblemRequest(

        @NotNull
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "문제 제목 수정", example = "문제 제목 수정")
        String title,

        @NotNull
        @ValidParameter
        @Schema(description = "제출 마감기한", example = "2021-08-31")
        String duration,

        @NotNull
        @Schema(description = "문제 내용 수정", example = "문제 내용 수정")
        String contents

) {
}

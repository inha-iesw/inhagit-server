package inha.git.problem.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateProblemRequest(
        @NotNull
        @Size(min = 1, max = 30, message = "문제 제목은 1자 이상 30자 이하로 입력해주세요.")
        @Schema(description = "문제 제목", example = "문제 제목")
        String title,

        @NotNull
        @Schema(description = "제출 마감기한", example = "2021-08-31")
        String duration,

        @NotNull
        @Size(min = 1, max = 250, message = "문제 내용은 1자 이상 250자 이하로 입력해주세요.")
        @Schema(description = "문제 내용", example = "문제 내용")
        String contents

) {
}

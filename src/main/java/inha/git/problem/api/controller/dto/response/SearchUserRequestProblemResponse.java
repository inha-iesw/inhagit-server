package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchUserRequestProblemResponse(
        @NotNull
        @Schema(description = "문제 신청자 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "문제 신청자 이름", example = "홍길동")
        String name
) {
}

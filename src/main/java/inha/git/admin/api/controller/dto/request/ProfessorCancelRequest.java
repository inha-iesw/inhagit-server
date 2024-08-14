package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProfessorCancelRequest(
        @NotNull(message = "교수 인덱스는 필수입니다.")
        @Schema(description = "교수 승인 취소할 유저 인덱스", example = "1")
        Integer userIdx
) {
}

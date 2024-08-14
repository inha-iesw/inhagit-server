package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AdminDemotionRequest(
        @NotNull(message = "관리자 인덱스는 필수입니다.")
        @Schema(description = "관리자 승격 취소할 유저 인덱스", example = "1")
        Integer userIdx
) {
}

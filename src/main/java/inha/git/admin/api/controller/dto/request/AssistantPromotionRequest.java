package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AssistantPromotionRequest(
        @NotNull(message = "학생 인덱스는 필수입니다.")
        @Schema(description = "조교로 승격될 학생 인덱스", example = "1")
        Integer userIdx
) {
}

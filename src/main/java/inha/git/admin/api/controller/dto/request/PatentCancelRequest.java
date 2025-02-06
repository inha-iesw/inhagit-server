package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PatentCancelRequest(
        @NotNull(message = "특허 인덱스는 필수입니다.")
        @Schema(description = "특허 승인 취소될 특허 인덱스", example = "1")
        Integer patentIdx
) {
}

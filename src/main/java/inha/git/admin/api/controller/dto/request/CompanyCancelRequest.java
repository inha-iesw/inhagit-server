package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CompanyCancelRequest(
        @NotNull(message = "기 인덱스는 필수입니다.")
        @Schema(description = "기업 승인 취소될 유저 인덱스", example = "1")
        Integer userIdx
) {
}

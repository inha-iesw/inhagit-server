package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserBlockRequest(
        @NotNull(message = "유저 인덱스는 필수입니다.")
        @Schema(description = "차단할 유저 유저 인덱스", example = "1")
        Integer userIdx
) {
}

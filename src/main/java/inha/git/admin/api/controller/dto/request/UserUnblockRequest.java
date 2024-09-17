package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserUnblockRequest(


        @NotNull(message = "유저 인덱스는 필수입니다.")
        @Schema(description = "차단해제할  유저 인덱스", example = "1")
        Integer userIdx
) {
}

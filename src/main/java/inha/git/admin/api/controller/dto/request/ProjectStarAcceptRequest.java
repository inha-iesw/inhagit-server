package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectStarAcceptRequest(
        @NotNull(message = "프로젝트 인덱스는 필수입니다.")
        @Schema(description = "프로젝트 Star 승인될 프로젝트 인덱스", example = "1")
        Integer projectIdx
) {
}

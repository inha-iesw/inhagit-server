package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchPatentSummaryResponse(

        @Schema(description = "특허 인덱스", example = "1", nullable = true)
        Integer idx,

        @Schema(description = "특허 승인 여부", example = "true", nullable = true)
        Boolean isAccepted
) {
}

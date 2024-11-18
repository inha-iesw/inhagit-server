package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchReportCond(

        @Schema(description = "신고타입 인덱스", example = "1")
        Integer reportTypeIdx,

        @Schema(description = "신고 원인 인덱스", example = "1")
        Integer reportReasonIdx,

        @Schema(description = "신고자 아이디", example = "1")
        Integer reporterId,

        @Schema(description = "신고 대상 아이디", example = "1")
        Integer reportedUserId
) {
}

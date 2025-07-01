package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import inha.git.common.BaseEntity;
import java.time.LocalDateTime;

public record ProjectStarResponses(

        @Schema(description = "project Star 인덱스", example = "1")
        Integer idx,

        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer projectIdx,

        @Schema(description = "등록 날짜", example = "2021-10-01T00:00:00")
        LocalDateTime acceptAt,

        @Schema(description = "생성일", example = "2025-06-29T04:45:00")
        LocalDateTime createdAt,

        @Schema(description = "상태", example = "ACTIVE")
        BaseEntity.State state,

        SearchUserResponse user
) {
}

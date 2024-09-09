package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectStatisticsResponse(

        @NotNull
        @Schema(description = "전체 프로젝트 수", example = "8")
        Integer totalProjectCount, // 전체 프로젝트 수

        @NotNull
        @Schema(description = "로컬로 등록된 프로젝트 수", example = "4")
        Integer localProjectCount, // 로컬  프로젝트 수

        @NotNull
        @Schema(description = "깃허브로 등록된 프로젝트 수", example = "4")
        Integer githubProjectCount, // 깃허브 프로젝트 수

        @NotNull
        @Schema(description = "프로젝트에 등록된 특허 수", example = "4")
        Integer patentProjectCount, // 특허 프로젝트 수

        @NotNull
        @Schema(description = "프로젝트를 업로드한 학생", example = "4")
        Integer projectUserCount,

        @NotNull
        @Schema(description = "특허를 등록한 학생", example = "4")
        Integer patentUserCount
) {
}

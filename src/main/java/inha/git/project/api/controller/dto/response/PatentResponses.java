package inha.git.project.api.controller.dto.response;

import inha.git.project.domain.enums.PatentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PatentResponses(

        @Schema(description = "특허 인덱스", example = "1")
        Integer idx,

        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer projectIdx,

        @Schema(description = "출원번호", example = "10-2021-1234567")
        String applicationNumber,

        @Schema(description = "특허 유형", example = "PATENT")
        PatentType patentType,

        @Schema(description = "출원인 이름", example = "김철수")
        String applicationDate,

        @Schema(description = "특허 한글명", example = "프로젝트 특허")
        String inventionTitle,

        @Schema(description = "특허 영문명", example = "Project Patent")
        String inventionTitleEnglish,

        @Schema(description = "등록 날짜", example = "2021-10-01T00:00:00")
        LocalDateTime createAt
) {
}

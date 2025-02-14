package inha.git.project.api.controller.dto.response;

import inha.git.project.domain.enums.PatentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchPatentResponse(

        @NotNull
        @Schema(description = "특허 idx", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "특허번호", example = "1020211234567")
        String applicationNumber,

        @NotNull
        @Schema(description = "특허 type", example = "PATENT")
        PatentType patentType,

        @NotNull
        @Schema(description = "출원일자", example = "2021-01-01T00:00:00")
        String applicationDate, // 출원일자

        @NotNull
        @Schema(description = "특허명", example = "특허명")
        String inventionTitle,

        @NotNull
        @Schema(description = "특허 영문명", example = "inventionTitleEnglish")
        String inventionTitleEnglish, // 특허 영문명

        @NotNull
        @Schema(description = "출원인 이름", example = "출원인 이름")
        String applicantName, // 출원인 이름
        @NotNull
        @Schema(description = "출원인 영문 이름", example = "applicantEnglishName")
        String applicantEnglishName, // 출원인 영문 이름

        @Schema(description = "특허 증빙 자료", example = "증빙자료 경로")
        String evidence,

        @Schema(description = "증빙자료명", example = "증빙자료명")
        String evidenceName,

        @Schema(description = "승인일", example = "2021-01-01T00:00:00")
        LocalDateTime acceptAt,

        @NotNull
        List<SearchInventorResponse> inventors
) {
}

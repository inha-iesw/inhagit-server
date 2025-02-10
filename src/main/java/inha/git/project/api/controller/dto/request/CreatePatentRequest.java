package inha.git.project.api.controller.dto.request;

import inha.git.project.domain.enums.PatentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePatentRequest(

        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer projectIdx,
        @NotNull
        @Schema(description = "특허번호", example = "1020211234567")
        String applicationNumber,

        @NotNull
        @Schema(description = "특허 유형", example = "PATENT")
        PatentType patentType, // 특허 유형

        @NotNull
        @Schema(description = "출원일자", example = "2021-01-01")
        String applicationDate, // 출원일자

        @NotNull
        @Schema(description = "특허명", example = "특허명")
        String inventionTitle,

        @Schema(description = "특허 영문명", example = "inventionTitleEnglish", nullable = true)
        String inventionTitleEnglish, // 특허 영문명

        @NotNull
        @Schema(description = "출원인 이름", example = "출원인 이름")
        String applicantName, // 출원인 이름

        @Schema(description = "출원인 영문 이름", example = "applicantEnglishName", nullable = true)
        String applicantEnglishName,// 출원인 영문 이름

        List<CreatePatentInventorRequest> inventors
) {
}

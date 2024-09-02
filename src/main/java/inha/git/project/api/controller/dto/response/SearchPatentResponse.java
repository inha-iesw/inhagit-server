package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SearchPatentResponse(
        @NotNull
        @Schema(description = "특허번호", example = "1020211234567")
        String applicationNumber,

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

        @NotNull
        List<SearchInventorResponse> inventors
) {
}

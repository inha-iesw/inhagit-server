package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

public record SearchInventorResponse(

        @NotNull
        @Schema(description = "발명자 이름", example = "발명자 이름")
        String name,

        @NotNull
        @Schema(description = "발명자 영문 이름", example = "inventorEnglishName")
        String englishName,

        @NotNull
        @Schema(description = "기관", example = "인하대학교")
        String affiliation,

        @NotNull
        @Schema(description = "지분", example = "26.42")
        String share,

        @NotNull
        @Schema(description = "주발명자 여부", example = "true")
        Boolean mainInventor,

        @NotNull
        @Schema(description = "이메일", example = "test@gmail.com")
        String email,

        @Schema(description = "학번/사번", example = "12312312")
        String userNumber
) {
}

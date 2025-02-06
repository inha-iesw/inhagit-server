package inha.git.project.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidEmail;
import inha.git.common.validation.annotation.ValidShare;
import inha.git.common.validation.annotation.ValidUserNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
public record CreatePatentInventorRequest(
        @NotNull
        @Schema(description = "발명자 이름", example = "발명자 이름")
        String name,
        @Schema(description = "발명자 영문 이름", example = "inventorEnglishName", nullable = true)
        String englishName,

        @NotNull
        @Schema(description = "발명자 소속", example = "인하대학교")
        String affiliation,

        @NotNull
        @ValidShare
        @Schema(description = "지분", example = "16.24")
        String share,

        @NotNull
        @Schema(description = "주 발명자 여부", example = "true")
        Boolean mainInventor,

        @NotNull
        @Email
        @ValidEmail
        @Schema(description = "발명자 이메일", example = "test@gmail.com")
        String email,

        @ValidUserNumber
        @Schema(description = "발명자 학번", example = "2018000000", nullable = true)
        String userNumber
) {
}

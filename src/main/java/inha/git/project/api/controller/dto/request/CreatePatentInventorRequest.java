package inha.git.project.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreatePatentInventorRequest(
        @NotNull
        @Schema(description = "발명자 이름", example = "발명자 이름")
        String name,

        @Schema(description = "발명자 영문 이름", example = "inventorEnglishName", nullable = true)
        String englishName
) {
}

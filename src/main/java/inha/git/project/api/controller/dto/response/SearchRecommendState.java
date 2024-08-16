package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchRecommendState(
       @NotNull
       @Schema(description = "founding 상태", example = "true")
       Boolean founding,

       @NotNull
       @Schema(description = "patent 상태", example = "fasle")
       Boolean patent,

       @NotNull
       @Schema(description = "registration 상태", example = "true")
       Boolean registration

) {
}

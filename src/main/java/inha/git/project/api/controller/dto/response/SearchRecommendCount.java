package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchRecommendCount(

       @NotNull
       @Schema(description = "founding 카운트", example = "1")
       Integer founding,

       @NotNull
       @Schema(description = "like 카운트", example = "1")
       Integer like,

       @NotNull
       @Schema(description = "registration 카운트", example = "1")
       Integer registration
) {
}

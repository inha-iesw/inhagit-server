package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchProjectPatentResponse(

        @NotNull
        @Schema(description = "프로젝트 idx", example = "1")
        Integer idx,

        @Schema(description = "깃허브 여부", example = "true")
        boolean isRepo
) {
}

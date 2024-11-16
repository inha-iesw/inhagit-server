package inha.git.project.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchPatentRequest(
        @NotNull
        @Schema(description = "특허번호", example = "1020211234567")
        String applicationNumber
) {
}

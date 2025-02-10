package inha.git.semester.controller.dto.request;

import inha.git.common.validation.annotation.SemesterNameUnique;
import inha.git.common.validation.annotation.ValidSemesterName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateSemesterRequest(

        @NotNull
        @SemesterNameUnique
        @ValidSemesterName
        @Schema(description = "학기 이름", example = "22-1학기")
        String name) {
}

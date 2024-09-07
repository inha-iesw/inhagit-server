package inha.git.college.controller.dto.request;

import inha.git.common.validation.annotation.CollegeNameUnique;
import inha.git.common.validation.annotation.ValidCollegeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateCollegeRequest(
        @NotNull
        @ValidCollegeName
        @CollegeNameUnique
        @Schema(description = "단과대 이름", example = "소프트웨어융합대학")
        String name) {
}

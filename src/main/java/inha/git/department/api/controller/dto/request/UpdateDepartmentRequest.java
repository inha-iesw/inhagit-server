package inha.git.department.api.controller.dto.request;

import inha.git.common.validation.annotation.DepartmentNameUnique;
import inha.git.common.validation.annotation.ValidDepartmentName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateDepartmentRequest(
        @NotNull
        @ValidDepartmentName
        @DepartmentNameUnique
        @Schema(description = "학과 이름", example = "컴퓨터공학과")
        String name) {
}

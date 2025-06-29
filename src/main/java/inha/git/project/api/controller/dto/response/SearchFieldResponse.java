package inha.git.project.api.controller.dto.response;

import inha.git.mapping.domain.ProjectField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchFieldResponse(
        @NotNull
        @Schema(description = "분야 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "분야 이름", example = "웹")
        String name
) {
        public static SearchFieldResponse from(ProjectField field) {
                return new SearchFieldResponse(
                        field.getField().getId(),
                        field.getField().getName()
                );
        }
}

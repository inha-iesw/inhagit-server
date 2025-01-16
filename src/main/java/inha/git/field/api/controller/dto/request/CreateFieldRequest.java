package inha.git.field.api.controller.dto.request;

import inha.git.common.validation.annotation.FieldNameLanguage;
import inha.git.common.validation.annotation.FieldNameUnique;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateFieldRequest(

        @NotNull
        @FieldNameUnique
        @FieldNameLanguage
        @Schema(description = "분야 이름", example = "웹")
        String name) {
}

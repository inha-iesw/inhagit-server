package inha.git.auth.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidName;
import inha.git.common.validation.annotation.ValidParameter;
import inha.git.common.validation.annotation.ValidUserNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FindEmailRequest(

        @NotNull
        @ValidName
        @ValidParameter
        @Schema(description = "이름", example = "황규혁")
        String name,

        @NotEmpty(message = "학번/사번은 필수 입력 항목입니다.")
        @ValidUserNumber
        @ValidParameter
        @Schema(description = "학번/사번", example = "12194118")
        String userNumber
) {
}

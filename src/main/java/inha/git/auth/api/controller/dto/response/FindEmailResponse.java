package inha.git.auth.api.controller.dto.response;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record FindEmailResponse(

        @NotNull
        @Email
        @ValidParameter
        @Schema(description = "유저 이메일", example = "ghkdr*****@inha.edu")
        String email
) {
}

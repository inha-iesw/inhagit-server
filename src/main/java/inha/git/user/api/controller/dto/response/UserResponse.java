package inha.git.user.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


public record UserResponse(

        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Integer idx
) { }

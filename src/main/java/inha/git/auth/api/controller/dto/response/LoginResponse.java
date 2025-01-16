package inha.git.auth.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record LoginResponse(

        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Integer userId,

        @NotNull
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTYzNzIwNjIwM30.1J9")
        String accessToken
) {
}

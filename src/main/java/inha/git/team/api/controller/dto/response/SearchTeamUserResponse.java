package inha.git.team.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchTeamUserResponse(

        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name,

        @NotNull
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email
) {
}

package inha.git.user.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchCompanyUserResponse (
        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "유저 이메일", example = "test@inha.ac.kr")
        String email,
        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name,
        @NotNull
        @Schema(description = "유저 포지션", example = "1")
        Integer position,
        @NotNull
        @Schema(description = "소속", example = "인하대학교")
        String affiliation
) implements SearchUserResponse{ }

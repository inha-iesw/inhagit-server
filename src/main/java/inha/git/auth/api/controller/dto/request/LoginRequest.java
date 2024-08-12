package inha.git.auth.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * LoginRequest는 로그인 요청 정보를 담는 DTO 클래스.
 */
public record LoginRequest(
        @NotNull
        @Schema(description = "유저 아이디", example = "testuser")
        String username,
        @NotNull
        @Schema(description = "비밀번호", example = "password")
        String password) {
}

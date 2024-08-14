package inha.git.admin.api.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import inha.git.common.validation.annotation.ValidName;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static inha.git.common.Constant.mapRoleToPosition;

public record SearchUserResponse(
        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Integer idx,

        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,
        @NotEmpty
        @ValidName
        @Schema(description = "이름", example = "홍길동")
        String name,
        @NotNull
        @Schema(description = "직책", example = "2")
        Integer position,
        @NotNull
        @Schema(description = "계정 생성일", example = "2024-05-31 04:26:56.831000 +00:00")
        LocalDateTime createdAt
) {
    @QueryProjection
    public SearchUserResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getName(),
                mapRoleToPosition(user.getRole()),
                user.getCreatedAt()
        );
    }



}

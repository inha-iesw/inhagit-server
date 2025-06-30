package inha.git.project.api.controller.dto.response;

import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import static inha.git.common.Constant.mapRoleToPosition;

public record SearchUserResponse(

        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name,

        @Schema(description = "유저 포지션", example = "1")
        Integer postion
) {
        public static SearchUserResponse from(User user) {
                if (user == null) {
                        return null;
                }

                return new SearchUserResponse(
                        user.getId(),
                        user.getName(),
                        mapRoleToPosition(user.getRole())
                );
        }
}

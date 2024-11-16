package inha.git.notice.api.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import inha.git.common.validation.annotation.ValidName;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SearchNoticeUserResponse(
        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Integer idx,
        @NotEmpty
        @ValidName
        @Schema(description = "이름", example = "홍길동")
        String name
) {
        @QueryProjection
        public SearchNoticeUserResponse(User user) {
                this(
                        user.getId(),
                        user.getName());
        }
}

package inha.git.common.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ReasonDTO는 응답 대한 상세 정보를 포함하는 데이터 전송 객체.
 */
@Builder
public record ReasonDTO(

            @Schema(description = "HTTP 상태", example = "OK")
            HttpStatus httpStatus,

            @Schema(description = "성공 여부", example = "true")
            boolean isSuccess,

            @Schema(description = "코드", example = "COMMON2000")
            String code,

            @Schema(description = "메시지", example = "성공입니다.")
            String message
) {
}

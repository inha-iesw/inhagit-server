package inha.git.common.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * ErrorReasonDTO는 에러에 대한 상세 정보를 포함하는 데이터 전송 객체.
 */
@Builder
public record ErrorReasonDTO(

        @Schema(description = "HTTP 상태 코드", example = "BAD_REQUEST")
        HttpStatus httpStatus,

        @Schema(description = "성공 여부", example = "false")
        boolean isSuccess,

        @Schema(description = "에러 코드", example = "COMMON4000")
        String code,

        @Schema(description = "에러 메시지", example = "필수 입력값이 누락되었습니다.")
        String message
)  {
}

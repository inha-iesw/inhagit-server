package inha.git.common.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ErrorReasonDTO는 에러에 대한 상세 정보를 포함하는 데이터 전송 객체.
 */
@Getter
@Builder
public class ErrorReasonDTO {

    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

}

package inha.git.common.code;

/**
 * BaseErrorCode는 에러 코드와 이유를 반환하는 메서드를 정의하는 인터페이스.
 */
public interface BaseErrorCode {

    /**
     * 에러 메시지와 코드를 포함하는 ErrorReasonDTO를 반환.
     *
     * @return 에러 메시지와 코드가 포함된 ErrorReasonDTO
     */
    ErrorReasonDTO getReason();

    /**
     * HTTP 상태와 에러 메시지, 코드를 포함하는 ErrorReasonDTO를 반환.
     *
     * @return HTTP 상태와 에러 메시지, 코드가 포함된 ErrorReasonDTO
     */
    ErrorReasonDTO getReasonHttpStatus();
}

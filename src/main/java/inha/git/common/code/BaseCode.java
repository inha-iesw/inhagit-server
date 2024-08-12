package inha.git.common.code;

/**
 * BaseCode는 에러 코드와 이유를 반환하는 메서드를 정의하는 인터페이스.
 */
public interface BaseCode {

    /**
     * 에러 메시지와 코드를 포함하는 ReasonDTO를 반환.
     *
     * @return 에러 메시지와 코드가 포함된 ReasonDTO
     */
    public ReasonDTO getReason();

    /**
     * HTTP 상태와 에러 메시지, 코드를 포함하는 ReasonDTO를 반환.
     *
     * @return HTTP 상태와 에러 메시지, 코드가 포함된 ReasonDTO
     */
    public ReasonDTO getReasonHttpStatus();
}

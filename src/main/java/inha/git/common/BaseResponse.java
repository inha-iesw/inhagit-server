package inha.git.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import inha.git.common.code.BaseCode;
import inha.git.common.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BaseResponse는 API 응답을 위한 공통 포맷을 제공하는 클래스.
 *
 * @param <T> 응답 데이터의 타입
 */
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    /**
     * 요청이 성공한 경우의 응답을 생성.
     *
     * @param result 응답 데이터
     * @param <T> 응답 데이터의 타입
     * @return 성공한 경우의 BaseResponse 객체
     */
    public static <T> BaseResponse<T> onSuccess(T result){
        return new BaseResponse<>(true, SuccessStatus.OK.getCode() , SuccessStatus.OK.getMessage(), result);
    }

    /**
     * 특정 코드와 함께 요청이 성공한 경우의 응답을 생성.
     *
     * @param code 응답 코드
     * @param result 응답 데이터
     * @param <T> 응답 데이터의 타입
     * @return 성공한 경우의 BaseResponse 객체
     */
    public static <T> BaseResponse<T> of(BaseCode code, T result){
        return new BaseResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), result);
    }


    /**
     * 요청이 실패한 경우의 응답을 생성.
     *
     * @param code 응답 코드
     * @param message 실패 메시지
     * @param data 추가 데이터
     * @param <T> 추가 데이터의 타입
     * @return 실패한 경우의 BaseResponse 객체
     */
    public static <T> BaseResponse<T> onFailure(String code, String message, T data){
        return new BaseResponse<>(false, code, message, data);
    }
}

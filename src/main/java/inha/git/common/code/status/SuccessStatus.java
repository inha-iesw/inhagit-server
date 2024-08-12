package inha.git.common.code.status;

import inha.git.common.code.BaseCode;
import inha.git.common.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    /**
     * 일반적인 응답
     * */
    OK(HttpStatus.OK, "COMMON2000", "성공입니다."),

    SIGN_UP_OK(HttpStatus.OK, "AUTH2000", "회원가입 성공"),
    LOGIN_OK(HttpStatus.OK, "AUTH2001", "로그인 성공"),
    REFRESH_OK(HttpStatus.OK, "AUTH2002", "리프레쉬토큰 재발급 성공"),
    LOGOUT_OK(HttpStatus.OK, "AUTH2003", "로그아웃 성공"),

    CREATE_PATIENT_OK(HttpStatus.OK, "PATIENT2000", "환자 추가 성공"),
    SEARCH_PATIENT_OK(HttpStatus.OK, "PATIENT2001", "환자 검색 성공"),
    EDIT_PATIENT_OK(HttpStatus.OK, "PATIENT2002", "환자 정보 수정 성공"),
    GET_PATIENT_OK(HttpStatus.OK, "PATIENT2003", "환자 정보 조회 성공"),
    DELETE_PATIENT_OK(HttpStatus.OK, "PATIENT2004", "환자 삭제 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
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

    LOGIN_OK(HttpStatus.OK, "AUTH2000", "로그인 성공"),
    //이메일 발송 성공
    EMAIL_SEND_OK(HttpStatus.CREATED, "AUTH2010", "이메일 발송 성공"),
    //이메일 인증 성공
    EMAIL_AUTH_OK(HttpStatus.CREATED, "AUTH2001", "이메일 인증 성공"),
    //학생 회원가입 성공
    STUDENT_SIGN_UP_OK(HttpStatus.CREATED, "AUTH2012", "학생 회원가입 성공"),
    PROFESSOR_SIGN_UP_OK(HttpStatus.CREATED, "AUTH2013", "교수 회원가입 성공"),
    COMPANY_SIGN_UP_OK(HttpStatus.CREATED, "AUTH2014", "기업 회원가입 성공"),
    LOGOUT_OK(HttpStatus.OK, "AUTH2003", "로그아웃 성공"),

    //관리자전용
    USER_SEARCH_OK(HttpStatus.OK, "ADMIN2000", "유저 검색 성공"),
    STUDENT_SEARCH_OK(HttpStatus.OK, "ADMIN2001", "학생 검색 성공"),
    PROFESSOR_SEARCH_OK(HttpStatus.OK, "ADMIN2002", "교수 검색 성공"),
    COMPANY_SEARCH_OK(HttpStatus.OK, "ADMIN2003", "기업 검색 성공"),
    PROMOTION_CREATED(HttpStatus.CREATED, "ADMIN2010", "관리자 승격 성공"),
    DEMOTION_CREATED(HttpStatus.CREATED, "ADMIN2011", "관리자 강등 성공"),

    DEPARTMENT_CREATE_OK(HttpStatus.CREATED, "DEPARTMENT2010", "학과 생성 성공"),
    DEPARTMENT_SEARCH_OK(HttpStatus.OK, "DEPARTMENT2000", "학과 전체 조회 성공"),
    DEPARTMENT_UPDATE_OK(HttpStatus.OK, "DEPARTMENT2001", "학과명 수정 성공"),
    DEPARTMENT_DELETE_OK(HttpStatus.OK, "DEPARTMENT2002", "학과 삭제 성공");

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
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
    PROFESSOR_ACCEPT_OK(HttpStatus.CREATED, "ADMIN2012", "교수 승인 성공"),
    PROFESSOR_CANCEL_OK(HttpStatus.CREATED, "ADMIN2013", "교수 승인 취소 성공"),
    COMPANY_ACCEPT_OK(HttpStatus.CREATED, "ADMIN2014", "기업 승인 성공"),
    COMPANY_CANCEL_OK(HttpStatus.CREATED, "ADMIN2015", "기업 승인 취소 성공"),

    DEPARTMENT_CREATE_OK(HttpStatus.CREATED, "DEPARTMENT2010", "학과 생성 성공"),
    DEPARTMENT_SEARCH_OK(HttpStatus.OK, "DEPARTMENT2000", "학과 전체 조회 성공"),
    DEPARTMENT_UPDATE_OK(HttpStatus.OK, "DEPARTMENT2001", "학과명 수정 성공"),
    DEPARTMENT_DELETE_OK(HttpStatus.OK, "DEPARTMENT2002", "학과 삭제 성공"),
    BANNER_CREATED_OK(HttpStatus.CREATED, "BANNER2010", "배너 생성 성공"),
    BANNER_SEARCH_OK(HttpStatus.OK, "BANNER2000", "배너 조회 성공"),
    FIELD_CREATE_OK(HttpStatus.CREATED, "FIELD2010", "분야 생성 성공"),
    FIELD_UPDATE_OK(HttpStatus.OK, "FIELD2001", "분야 수정 성공"),
    FIELD_DELETE_OK(HttpStatus.OK, "FIELD2002", "분야 삭제 성공"),
    FIELD_SEARCH_OK(HttpStatus.OK, "FIELD2000", "분야 전체 조회 성공"),

    NOTICE_CREATE_OK(HttpStatus.CREATED, "NOTICE2010", "공지 생성 성공"),
    NOTICE_UPDATE_OK(HttpStatus.OK, "NOTICE2001", "공지 수정 성공"),
    NOTICE_DELETE_OK(HttpStatus.OK, "NOTICE2002", "공지 삭제 성공"),
    NOTICE_SEARCH_OK(HttpStatus.OK, "NOTICE2000", "공지 전체 조회 성공"),
    NOTICE_DETAIL_OK(HttpStatus.OK, "NOTICE2003", "공지 상세 조회 성공"),

    PROJECT_CREATE_OK(HttpStatus.CREATED, "PROJECT2010", "프로젝트 생성 성공"),
    PROJECT_UPDATE_OK(HttpStatus.OK, "PROJECT2001", "프로젝트 수정 성공"),
    PROJECT_DELETE_OK(HttpStatus.OK, "PROJECT2002", "프로젝트 삭제 성공"),
    PROJECT_SEARCH_OK(HttpStatus.OK, "PROJECT2000", "프로젝트 전체 조회 성공"),
    PROJECT_DETAIL_OK(HttpStatus.OK, "PROJECT2003", "프로젝트 상세 조회 성공");

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
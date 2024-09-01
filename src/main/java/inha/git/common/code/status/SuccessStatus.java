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
    USER_DETAIL_OK(HttpStatus.OK, "ADMIN2004", "유저 개별 조회 성공"),
    PROMOTION_CREATED(HttpStatus.CREATED, "ADMIN2010", "관리자 승격 성공"),
    DEMOTION_CREATED(HttpStatus.CREATED, "ADMIN2011", "관리자 강등 성공"),
    PROFESSOR_ACCEPT_OK(HttpStatus.CREATED, "ADMIN2012", "교수 승인 성공"),
    PROFESSOR_CANCEL_OK(HttpStatus.CREATED, "ADMIN2013", "교수 승인 취소 성공"),
    COMPANY_ACCEPT_OK(HttpStatus.CREATED, "ADMIN2014", "기업 승인 성공"),
    COMPANY_CANCEL_OK(HttpStatus.CREATED, "ADMIN2015", "기업 승인 취소 성공"),
    ASSISTANT_PROMOTION_OK(HttpStatus.CREATED, "ADMIN2016", "조교 승격 성공"),
    ASSISTANT_PROMOTION_CANCEL_OK(HttpStatus.CREATED, "ADMIN2017", "조교 승격 취소 성공"),


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
    PROJECT_DETAIL_OK(HttpStatus.OK, "PROJECT2003", "프로젝트 상세 조회 성공"),
    FILE_SEARCH_OK(HttpStatus.OK, "PROJECT2004", "프로젝트 파일 조회 성공"),
    FOUNDING_RECOMMEND_SUCCESS(HttpStatus.CREATED, "PROJECT2011", "창업 추천 성공"),
    PATENT_RECOMMEND_SUCCESS(HttpStatus.CREATED, "PROJECT2012", "특허 추천 성공"),
    REGISTRATION_RECOMMEND_SUCCESS(HttpStatus.CREATED, "PROJECT2013", "등록 추천 성공"),
    FOUNDING_RECOMMEND_CANCEL_SUCCESS(HttpStatus.CREATED, "PROJECT2014", "창업 추천 취소 성공"),
    PATENT_RECOMMEND_CANCEL_SUCCESS(HttpStatus.CREATED, "PROJECT2015", "특허 추천 취소 성공"),
    REGISTRATION_RECOMMEND_CANCEL_SUCCESS(HttpStatus.CREATED, "PROJECT2016", "등록 추천 취소 성공"),
    PROJECT_COMMENT_CREATE_OK(HttpStatus.CREATED, "PROJECT2017", "프로젝트 댓글 생성 성공"),
    PROJECT_COMMENT_DELETE_OK(HttpStatus.OK, "PROJECT2018", "프로젝트 댓글 삭제 성공"),
    PROJECT_COMMENT_UPDATE_OK(HttpStatus.OK, "PROJECT2019", "프로젝트 댓글 수정 성공"),
    PROJECT_COMMENT_SEARCH_OK(HttpStatus.OK, "PROJECT2020", "프로젝트 댓글 전체 조회 성공"),
    PROJECT_COMMENT_REPLY_CREATE_OK(HttpStatus.CREATED, "PROJECT2021", "프로젝트 대댓글 생성 성공"),
    PROJECT_COMMENT_REPLY_DELETE_OK(HttpStatus.OK, "PROJECT2022", "프로젝트 대댓글 삭제 성공"),
    PROJECT_COMMENT_REPLY_UPDATE_OK(HttpStatus.OK, "PROJECT2023", "프로젝트 대댓글 수정 성공"),

    GITHUB_TOKEN_REFRESH_OK(HttpStatus.OK, "GITHUB2000", "깃허브 토큰 갱신 성공"),
    GITHUB_REPOSITORIES_OK(HttpStatus.OK, "GITHUB2001", "깃허브 레포지토리 조회 성공"),

    PROBLEM_CREATE_OK(HttpStatus.CREATED, "PROBLEM2010", "문제 생성 성공"),
    PROBLEM_SEARCH_OK(HttpStatus.OK, "PROBLEM2000", "문제 전체 조회 성공"),
    PROBLEM_UPDATE_OK(HttpStatus.OK, "PROBLEM2001", "문제 수정 성공"),
    PROBLEM_DELETE_OK(HttpStatus.OK, "PROBLEM2002", "문제 삭제 성공"),
    PROBLEM_DETAIL_OK(HttpStatus.OK, "PROBLEM2003", "문제 상세 조회 성공"),
    PROBLEM_REQUEST_USER_OK(HttpStatus.CREATED, "PROBLEM2011", "문제 개인 참여 성공"),
    PROBLEM_REQUEST_TEAM_OK(HttpStatus.CREATED, "PROBLEM2012", "문제 팀 참여 성공"),
    PROBLEM_APPROVE_OK(HttpStatus.CREATED, "PROBLEM2013", "문제 참여 승인 성공"),
    PROBLEM_REQUEST_SEARCH_OK(HttpStatus.OK, "PROBLEM2004", "문제 신청 목록 조회 성공"),

    QUESTION_CREATE_OK(HttpStatus.CREATED, "QUESTION2010", "질문 생성 성공"),
    QUESTION_SEARCH_OK(HttpStatus.OK, "QUESTION2000", "질문 전체 조회 성공"),
    QUESTION_UPDATE_OK(HttpStatus.OK, "QUESTION2001", "질문 수정 성공"),
    QUESTION_DELETE_OK(HttpStatus.OK, "QUESTION2002", "질문 삭제 성공"),
    QUESTION_DETAIL_OK(HttpStatus.OK, "QUESTION2003", "질문 상세 조회 성공"),
    QUESTION_COMMENT_CREATE_OK(HttpStatus.CREATED, "QUESTION2011", "질문 댓글 생성 성공"),
    QUESTION_COMMENT_DELETE_OK(HttpStatus.OK, "QUESTION2012", "질문 댓글 삭제 성공"),
    QUESTION_COMMENT_UPDATE_OK(HttpStatus.OK, "QUESTION2013", "질문 댓글 수정 성공"),
    QUESTION_COMMENT_SEARCH_OK(HttpStatus.OK, "QUESTION2014", "질문 댓글 전체 조회 성공"),
    QUESTION_COMMENT_REPLY_CREATE_OK(HttpStatus.CREATED, "QUESTION2015", "질문 대댓글 생성 성공"),
    QUESTION_COMMENT_REPLY_DELETE_OK(HttpStatus.OK, "QUESTION2016", "질문 대댓글 삭제 성공"),
    QUESTION_COMMENT_REPLY_UPDATE_OK(HttpStatus.OK, "QUESTION2017", "질문 대댓글 수정 성공"),

    //팀
    TEAM_CREATE_OK(HttpStatus.CREATED, "TEAM2010", "팀 생성 성공"),
    TEAM_SEARCH_OK(HttpStatus.OK, "TEAM2000", "팀 전체 조회 성공"),
    TEAM_UPDATE_OK(HttpStatus.OK, "TEAM2001", "팀 수정 성공"),
    TEAM_DELETE_OK(HttpStatus.OK, "TEAM2002", "팀 삭제 성공"),
    TEAM_DETAIL_OK(HttpStatus.OK, "TEAM2003", "팀 상세 조회 성공"),
    TEAM_GET_MY_TEAMS_OK(HttpStatus.OK, "TEAM2004", "내가 생성한 팀 목록 가져오기 성공"),
    TEAM_JOIN_OK(HttpStatus.CREATED, "TEAM2011", "팀 가입 성공"),
    TEAM_JOIN_ACCEPT_OK(HttpStatus.CREATED, "TEAM2012", "팀 가입 승인 성공"),
    TEAM_EXIT_OK(HttpStatus.OK, "TEAM2013", "팀 탈퇴 성공"),
    TEAM_POST_CREATE_OK(HttpStatus.CREATED, "TEAM2014", "팀 게시글 생성 성공"),
    TEAM_POST_UPDATE_OK(HttpStatus.OK, "TEAM2015", "팀 게시글 수정 성공"),
    TEAM_POST_DELETE_OK(HttpStatus.OK, "TEAM2016", "팀 게시글 삭제 성공"),
    TEAM_POST_SEARCH_OK(HttpStatus.OK, "TEAM2017", "팀 게시글 전체 조회 성공"),
    TEAM_POST_DETAIL_OK(HttpStatus.OK, "TEAM2018", "팀 게시글 상세 조회 성공"),
    TEAM_COMMENT_CREATE_OK(HttpStatus.CREATED, "TEAM2019", "팀 게시글 댓글 생성 성공"),
    TEAM_COMMENT_DELETE_OK(HttpStatus.OK, "TEAM2020", "팀 게시글 댓글 삭제 성공"),
    TEAM_COMMENT_UPDATE_OK(HttpStatus.OK, "TEAM2021", "팀 게시글 댓글 수정 성공"),
    TEAM_COMMENT_SEARCH_OK(HttpStatus.OK, "TEAM2022", "팀 게시글 댓글 전체 조회 성공"),
    TEAM_COMMENT_REPLY_CREATE_OK(HttpStatus.CREATED, "TEAM2023", "팀 게시글 대댓글 생성 성공"),
    TEAM_COMMENT_REPLY_DELETE_OK(HttpStatus.OK, "TEAM2024", "팀 게시글 대댓글 삭제 성공"),
    TEAM_COMMENT_REPLY_UPDATE_OK(HttpStatus.OK, "TEAM2025", "팀 게시글 대댓글 수정 성공"),

    MY_PAGE_USER_SEARCH_OK(HttpStatus.OK, "USER2000", "마이페이지 유저 조회 성공"),
    MY_PAGE_PROJECT_SEARCH_OK(HttpStatus.OK, "USER2001", "마이페이지 프로젝트 조회 성공"),
    MY_PAGE_QUESTION_SEARCH_OK(HttpStatus.OK, "USER2002", "마이페이지 질문 조회 성공"),
    MY_PAGE_TEAM_SEARCH_OK(HttpStatus.OK, "USER2003", "마이페이지 팀 조회 성공"),
    PW_CHANGE_OK(HttpStatus.OK, "USER2004", "비밀번호 변경 성공"),

    PROJECT_STATISTICS_SEARCH_OK(HttpStatus.OK, "STATISTICS2000", "프로젝트 통계 조회 성공"),
    TEAM_STATISTICS_SEARCH_OK(HttpStatus.OK, "STATISTICS2001", "팀 통계 조회 성공"),
    QUESTION_STATISTICS_SEARCH_OK(HttpStatus.OK, "STATISTICS2002", "질문 통계 조회 성공"),

    PATENT_SEARCH_OK(HttpStatus.OK, "PATENT2000", "특허 조회 성공");

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
package inha.git.common.code.status;

import inha.git.common.code.BaseErrorCode;
import inha.git.common.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ErrorStatus는 서버 응답 시 사용되는 에러 코드를 정의.
 */
@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    /**
     * 400 : Request, Response 오류
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON4001", "로그인 인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "접근 권한이 없는 요청입니다."),
    RESPONSE_ERROR(HttpStatus.NOT_FOUND, "COMMON4004", "값을 불러오는데 실패하였습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON4005", "입력값이 올바르지 않습니다."),
    DUPLICATION_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4006", "중복된 요청입니다."),

    USERS_EMPTY_EMAIL( HttpStatus.BAD_REQUEST, "USER4000", "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(HttpStatus.BAD_REQUEST,"USER4001" , "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER4002","중복된 아이디입니다."),
    FAILED_TO_LOGIN(HttpStatus.BAD_REQUEST, "USER4003", "존재하지 않는 아이디거나 비밀번호가 틀렸습니다."),
    NOT_FIND_USER(HttpStatus.NOT_FOUND, "USER4000", "일치하는 유저가 없습니다."),
    EXIST_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER4005", "이미 존재하는 전화번호입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER4006", "핸드폰 번호 양식에 맞지 않습니다. 예시: +82-10-0000-0000"),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "USER4004", "이미 존재하는 사용자입니다."),
    NOT_APPROVED_USER(HttpStatus.BAD_REQUEST, "USER4007", "아직 승인되지 않은 계정입니다."),
    NOT_AUTHORIZED_USER(HttpStatus.FORBIDDEN, "USER4008", "사용자 관련 정보에 접근할 권한이 없습니다."),
    INVALID_EMAIL_DOMAIN(HttpStatus.BAD_REQUEST, "USER4009", "이메일 도메인이 유효하지 않습니다."),
    INVALID_STUDENT_NUMBER(HttpStatus.BAD_REQUEST, "USER4010", "학번/사번으로 이루어질 수 없습니다."),
    ACCOUNT_LOCKED(HttpStatus.BAD_REQUEST, "USER4011", "비밀번호 5회 연속 실패로 계정이 잠겼습니다. 10분 뒤에 다시 시도해주세요."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER4012", "중복된 이메일입니다."),

    NOT_EXIST_BUG_REPORT(HttpStatus.BAD_REQUEST, "BUG_REPORT4000", "존재하지 않는 버그 제보입니다."),
    NOT_AUTHORIZED_BUG_REPORT(HttpStatus.BAD_REQUEST, "BUG_REPORT4001", "버그 제보를 수정할 권한이 없습니다."),
    NOT_ALLOWED_DELETE_BUG_REPORT(HttpStatus.BAD_REQUEST, "BUG_REPORT4002", "버그 제보를 삭제할 권한이 없습니다."),

    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT4000", "JWT를 입력해주세요"),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT4001", "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(HttpStatus.FORBIDDEN, "JWT4002", "권한이 없는 유저의 접근입니다."),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "JWT4003","인증 헤더가 없습니다."),
    INVALID_JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT4004", "만료된 JWT입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT4005", "유효하지 않은 서명입니다."),

    KAKAO_TOKEN_RECEIVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN4000", "카카오 서버로부터 액세스 토큰을 받는데 실패했습니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4001", "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002,", "유효하지 않은 토큰입니다."),

    INVALID_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "OAUTH4000", "알 수 없는 소셜 로그인 형식입니다."),

    INVALID_PAGE(HttpStatus.BAD_REQUEST, "PAGE4000", "페이지는 1 이상이어야 합니다."),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "PAGE4001", "사이즈는 1 이상이어야 합니다."),

    JSON_CONVERT_ERROR(HttpStatus.BAD_REQUEST, "JSON4000", "JSON 변환에 실패하였습니다."),

    REPORT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4000", "신고 타입을 찾을 수 없습니다."),
    REPORT_REASON_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4001", "신고 사유를 찾을 수 없습니다."),
    DUPLICATE_REPORT(HttpStatus.BAD_REQUEST, "REPORT4002", "이미 신고한 게시글입니다."),
    CANNOT_REPORT_MYSELF(HttpStatus.BAD_REQUEST, "REPORT4003", "자신을 신고할 수 없습니다."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT4004", "신고를 찾을 수 없습니다."),
    CANNOT_DELETE_REPORT(HttpStatus.BAD_REQUEST, "REPORT4005", "신고를 삭제할 수 없습니다."),

    S3_UPLOAD(HttpStatus.BAD_REQUEST, "S3UPLOAD4001", "S3 파일 업로드 실패."),

    FAILED_TO_CONVERT_MULTIPARTFILE_RESOURCE(HttpStatus.BAD_REQUEST, "FILE4000", "MultipartFileResource 변환에 실패하였습니다."),
    FILE_CONVERT(HttpStatus.BAD_REQUEST, "FILE4001", "파일 변환 실패."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FILE4002", "파일을 찾을 수 없습니다."),
    FILE_NOT_ZIP(HttpStatus.BAD_REQUEST, "FILE4003", "zip 파일만 업로드 가능합니다."),
    FILE_MAX_FILES(HttpStatus.BAD_REQUEST, "FILE4004", "압축 파일 내의 파일 수는 100개 이하로 제한됩니다."),
    FILE_MAX_SIZE(HttpStatus.BAD_REQUEST, "FILE4005", "압축 파일의 총 크기는 20MB 이하로 제한됩니다."),
    FILE_DELETE_FAIL(HttpStatus.BAD_REQUEST, "FILE4006", "파일 삭제에 실패하였습니다."),
    FILE_COMPRESS_FAIL(HttpStatus.BAD_REQUEST, "FILE4007", "파일 압축에 실패하였습니다."),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "FILE4008", "지원하지 않는 파일 형식입니다."),
    FILE_UNZIP_ERROR(HttpStatus.BAD_REQUEST, "FILE4009", "파일 압축 해제에 실패하였습니다."),
    FILE_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "FILE4010", "파일 처리에 실패하였습니다."),
    FILE_INVALID_NAME(HttpStatus.BAD_REQUEST, "FILE4011", "파일 이름이 유효하지 않습니다."),
    INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "FILE4012", "유효하지 않은 파일 경로입니다."),

    EXCEL_CREATE_ERROR(HttpStatus.BAD_REQUEST, "EXCEL4000", "엑셀 파일 생성 중 오류 발생"),

    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL4001", "이메일이 존재하지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "EMAIL4002", "이미 존재하는 이메일입니다."),
    EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "EMAIL4003", "이메일 전송에 실패했습니다."),
    EMAIL_AUTH_FAIL(HttpStatus.BAD_REQUEST, "EMAIL4004", "이메일 인증에 실패했습니다."),
    EMAIL_AUTH_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL4005", "이메일 인증 시간이 만료되었습니다."),
    EMAIL_AUTH_ALREADY(HttpStatus.BAD_REQUEST, "EMAIL4006", "이미 인증된 이메일입니다."),
    EMAIL_AUTH_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL4007", "이메일 인증을 먼저 진행해주세요."),
    EMAIL_AUTH_NOT_MATCH(HttpStatus.BAD_REQUEST, "EMAIL4008", "이메일 인증번호가 일치하지 않습니다."),

    BANNER_FILE_EMPTY(HttpStatus.BAD_REQUEST, "BANNER4000", "배너 파일이 비어있습니다."),

    FIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "FIELD4000", "분야를 찾을 수 없습니다"),

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE4000", "해당 공지가 존재하지 않습니다."),
    NOTICE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "NOTICE4001", "해당 공지를 수정할 권한이 없습니다."),

    COMPANY_CANNOT_CREATE_PROJECT(HttpStatus.BAD_REQUEST, "PROJECT4000", "기업 회원은 프로젝트를 생성할 수 없습니다."),
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4001", "프로젝트를 찾을 수 없습니다."),
    PROJECT_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PROJECT4002", "프로젝트를 수정할 권한이 없습니다."),
    MY_PROJECT_RECOMMEND(HttpStatus.BAD_REQUEST, "PROJECT4003", "자신의 프로젝트에는 추천할 수 없습니다."),
    PROJECT_ALREADY_RECOMMEND(HttpStatus.BAD_REQUEST, "PROJECT4004", "이미 추천한 프로젝트입니다."),
    PROJECT_NOT_RECOMMEND(HttpStatus.BAD_REQUEST, "PROJECT4005", "추천하지 않은 프로젝트입니다."),
    PROJECT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4006", "프로젝트 댓글을 찾을 수 없습니다."),
    PROJECT_COMMENT_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PROJECT4007", "댓글을 수정할 권한이 없습니다."),
    PROJECT_COMMENT_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PROJECT4008", "댓글을 삭제할 권한이 없습니다."),
    PROJECT_COMMENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4009", "프로젝트 대댓글을 찾을 수 없습니다."),
    PROJECT_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PROJECT4010", "대댓글을 수정할 권한이 없습니다."),
    PROJECT_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "PROJECT4011", "프로젝트를 삭제할 권한이 없습니다."),
    PATENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4012", "특허를 찾을 수 없습니다."),
    ALREADY_REGISTERED_PATENT(HttpStatus.BAD_REQUEST, "PROJECT4013", "이미 등록된 특허입니다."),
    USER_NOT_INVENTORY(HttpStatus.BAD_REQUEST, "PROJECT4014", "해당 특허에 참여하지 않은 유저입니다."),
    USER_NOT_PROJECT_OWNER(HttpStatus.BAD_REQUEST, "PROJECT4015", "프로젝트의 소유자가 아닙니다."),
    PROJECT_UPLOAD_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT4016", "프로젝트 업로드 파일을 찾을 수 없습니다."),
    PROJECT_COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "PROJECT4017", "이미 삭제된 댓글입니다."),
    MY_PROJECT_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4018", "자신의 프로젝트에는 좋아요를 누를 수 없습니다."),
    PROJECT_ALREADY_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4019", "이미 좋아요한 프로젝트입니다."),
    PROJECT_NOT_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4020", "좋아요하지 않은 프로젝트입니다."),
    PROJECT_NOT_PUBLIC(HttpStatus.BAD_REQUEST, "PROJECT4021", "비공개 프로젝트입니다."),
    ALREADY_RECOMMENDED(HttpStatus.BAD_REQUEST, "PROJECT4022", "이미 추천한 프로젝트입니다."),
    ALREADY_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4017", "이미 좋아요한 댓글입니다."),
    MY_COMMENT_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4018", "자신의 댓글에는 좋아요를 누를 수 없습니다."),
    NOT_LIKE(HttpStatus.BAD_REQUEST, "PROJECT4019", "좋아요하지 않은 댓글입니다."),

    TEMPORARY_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE4000", "일시적으로 서비스를 이용할 수 없습니다."),

    GITHUB_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "GITHUB4000", "깃허브 토큰이 등록되지 않았습니다. 깃허브 토큰을 먼저 등록해주세요."),
    INVALID_GITHUB_TOKEN(HttpStatus.BAD_REQUEST, "GITHUB4001", "유효하지 않은 GitHub 토큰입니다."),
    FAILED_TO_GET_GITHUB_REPOSITORIES(HttpStatus.BAD_REQUEST, "GITHUB4002", "깃허브 레포지토리 목록을 가져오는데 실패했습니다."),
    GITHUB_CLONE_ERROR(HttpStatus.BAD_REQUEST, "GITHUB4003", "깃허브 레포지토리 클론에 실패했습니다."),
    GITHUB_REPO_NOT_FOUND(HttpStatus.BAD_REQUEST, "GITHUB4004", "깃허브로 등록된 프로젝트가 아닙니다."),

    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT4000", "학과를 찾을 수 없습니다."),
    DEPARTMENT_NOT_BELONG_TO_COLLEGE(HttpStatus.BAD_REQUEST, "DEPARTMENT4001", "해당 학과는 해당 단과대에 속하지 않습니다."),

    COLLEGE_NOT_FOUND(HttpStatus.NOT_FOUND, "COLLEGE4000", "단과대를 찾을 수 없습니다."),

    SEMESTER_NOT_FOUND(HttpStatus.NOT_FOUND, "SEMESTER4000", "학기를 찾을 수 없습니다."),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY4000", "카테고리를 찾을 수 없습니다."),

    COLLEGE_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "COLLEGE4001", "단과대 통계를 찾을 수 없습니다."),

    ALREADY_ADMIN(HttpStatus.BAD_REQUEST, "ADMIN4000", "이미 관리자 권한이 있습니다."),
    NOT_ADMIN(HttpStatus.BAD_REQUEST, "ADMIN4001", "이미 관리자 권한이 없습니다."),
    NOT_PROFESSOR(HttpStatus.BAD_REQUEST, "ADMIN4002", "교수 계정이 아닙니다."),
    ALREADY_ACCEPTED_PROFESSOR(HttpStatus.BAD_REQUEST, "ADMIN4003", "이미 승인된 교수입니다."),
    NOT_ACCEPTED_PROFESSOR(HttpStatus.BAD_REQUEST, "ADMIN4004", "승인되지 않은 교수입니다."),
    NOT_COMPANY(HttpStatus.BAD_REQUEST, "ADMIN4005", "기업 계정이 아닙니다."),
    ALREADY_ACCEPTED_COMPANY(HttpStatus.BAD_REQUEST, "ADMIN4006", "이미 승인된 기업입니다."),
    NOT_ACCEPTED_COMPANY(HttpStatus.BAD_REQUEST, "ADMIN4007", "승인되지 않은 기업입니다."),
    NOT_STUDENT(HttpStatus.BAD_REQUEST, "ADMIN4008", "학생 계정이 아닙니다."),
    NOT_ASSISTANT(HttpStatus.BAD_REQUEST, "ADMIN4009", "조교 계정이 아닙니다."),
    ALREADY_BLOCKED_USER(HttpStatus.BAD_REQUEST, "ADMIN4010", "이미 차단된 사용자입니다."),
    NOT_BLOCKED_USER(HttpStatus.BAD_REQUEST, "ADMIN4011", "차단되지 않은 사용자입니다."),
    CANNOT_BLOCK_ADMIN(HttpStatus.BAD_REQUEST, "ADMIN4012", "관리자 계정은 차단할 수 없습니다."),
    BLOCKED_USER(HttpStatus.BAD_REQUEST, "ADMIN4013", "차단된 사용자입니다."),

    NOT_EXIST_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4000", "해당 문제가 존재하지 않습니다."),
    NOT_AUTHORIZED_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4001", "문제를 수정할 권한이 없습니다."),
    NOT_ALLOWED_PARTICIPATE(HttpStatus.BAD_REQUEST, "PROBLEM4002", "출제자는 참여할 수 없습니다."),
    PROBLEM_DEADLINE_PASSED(HttpStatus.BAD_REQUEST, "PROBLEM4003", "문제의 마감 기한이 지났습니다."),
    COMPANY_PROFESSOR_CANNOT_PARTICIPATE(HttpStatus.BAD_REQUEST, "PROBLEM4004", "기업 또는 교수는 문제에 참여할 수 없습니다."),
    ALREADY_REQUESTED_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4005", "이미 문제에 참여 요청을 한 상태입니다."),
    ALREADY_PARTICIPATED_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4006", "이미 참여중인 문제입니다."),
    ONLY_LEADER_CAN_APPLY(HttpStatus.BAD_REQUEST, "PROBLEM4007", "팀장만 신청할 수 있습니다."),
    NOT_EXIST_REQUEST_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4008", "해당 문제 참여 요청이 존재하지 않습니다."),
    NOT_ALLOWED_APPROVE(HttpStatus.BAD_REQUEST, "PROBLEM4009", "문제 출제자만 승인할 수 있습니다."),
    ALREADY_APPROVED_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4010", "이미 승인된 요청입니다."),
    NOT_ALLOWED_VIEW_PARTICIPANT(HttpStatus.BAD_REQUEST, "PROBLEM4011", "출제자만 해당 문제 참여자를 볼 수 있습니다."),
    NOT_EXIST_PERSONAL_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4012", "해당 문제 개인 참여 요청이 존재하지 않습니다."),
    NOT_EXIST_TEAM_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4013", "해당 문제 팀 참여 요청이 존재하지 않습니다."),
    NOT_ALLOWED_VIEW_SUBMIT(HttpStatus.BAD_REQUEST, "PROBLEM4014", "출제자만 해당 문제 제출물을 볼 수 있습니다."),
    NOT_ALLOWED_CHECK_SUBMIT(HttpStatus.BAD_REQUEST, "PROBLEM4015", "출제자는 문제 제출 가능 여부 확인을 할 수 없습니다."),
    NOT_ALLOWED_SUBMIT(HttpStatus.BAD_REQUEST, "PROBLEM4016", "출제자는 문제를 제출할 수 없습니다."),
    NOT_ALLOWED_SUBMIT_PERSONAL(HttpStatus.BAD_REQUEST, "PROBLEM4017", "문제 참여가 승인되지 않았습니다."),
    NOT_PERSONAL_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4018", "개인 문제 요청이 아닙니다."),
    NOT_PARTICIPANT(HttpStatus.BAD_REQUEST, "PROBLEM4019", "참여자 본인이 아닙니다."),
    NOT_TEAM_LEADER(HttpStatus.BAD_REQUEST, "PROBLEM4020", "팀장만 제출할 수 있습니다."),
    NOT_TEAM_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4020", "팀 문제 요청이 아닙니다."),
    NOT_TEAM_PARTICIPANT(HttpStatus.BAD_REQUEST, "PROBLEM4021", "참여자 팀이 아닙니다."),
    ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "PROBLEM4022", "이미 제출한 문제입니다."),
    NOT_ALLOWED_SUBMIT_TEAM(HttpStatus.BAD_REQUEST, "PROBLEM4023", "팀 문제 참여가 승인되지 않았습니다."),
    NOT_AUTHORIZED_PROBLEM_REQUEST(HttpStatus.BAD_REQUEST, "PROBLEM4024", "문제 참여 요청을 수정할 권한이 없습니다."),
    PROBLEM_REQUEST_CANNOT_BE_MODIFIED(HttpStatus.BAD_REQUEST, "PROBLEM4025", "현재는 문제 참여 요청을 수정할 수 없습니다."),
    PROBLEM_REQUEST_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "PROBLEM4026", "현재는 문제 참여 요청을 삭제할 수 없습니다."),
    NOT_ALLOWED_VIEW_REQUEST_PROBLEM(HttpStatus.BAD_REQUEST, "PROBLEM4027", "출제자만 해당 문제 신청 목록을 볼 수 있습니다."),

    COMPANY_CANNOT_CREATE_QUESTION(HttpStatus.BAD_REQUEST, "QUESTION4000", "기업 회원은 질문을 생성할 수 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION4001", "질문을 찾을 수 없습니다."),
    QUESTION_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4002", "질문을 수정할 권한이 없습니다."),
    QUESTION_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4003", "질문을 삭제할 권한이 없습니다."),
    QUESTION_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION4004", "질문 댓글을 찾을 수 없습니다."),
    QUESTION_COMMENT_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4005", "댓글을 수정할 권한이 없습니다."),
    QUESTION_COMMENT_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4006", "댓글을 삭제할 권한이 없습니다."),
    QUESTION_COMMENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION4007", "질문 대댓글을 찾을 수 없습니다."),
    QUESTION_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4008", "대댓글을 수정할 권한이 없습니다."),
    QUESTION_COMMENT_REPLY_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "QUESTION4009", "대댓글을 삭제할 권한이 없습니다."),
    MY_QUESTION_LIKE(HttpStatus.BAD_REQUEST, "QUESTION4010", "자신의 질문에는 좋아요를 누를 수 없습니다."),
    QUESTION_ALREADY_LIKE(HttpStatus.BAD_REQUEST, "QUESTION4011", "이미 좋아요한 질문입니다."),
    QUESTION_NOT_LIKE(HttpStatus.BAD_REQUEST, "QUESTION4012", "좋아요하지 않은 질문입니다."),
    QUESTION_COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "QUESTION4013", "이미 삭제된 댓글입니다."),

    COMPANY_CANNOT_CREATE_TEAM(HttpStatus.BAD_REQUEST, "TEAM4000", "기업 회원은 팀을 생성할 수 없습니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4001", "팀을 찾을 수 없습니다."),
    TEAM_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "TEAM4002", "팀을 수정할 권한이 없습니다."),
    TEAM_MAX_MEMBER(HttpStatus.BAD_REQUEST, "TEAM4003", "최대 가입 인원 수는 현재 팀에 가입된 팀원 수 보다 적을 수 없습니다."),
    TEAM_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "TEAM4004", "팀을 삭제할 권한이 없습니다."),
    COMPANY_CANNOT_JOIN_TEAM(HttpStatus.BAD_REQUEST, "TEAM4005", "기업 회원은 팀에 가입할 수 없습니다."),
    TEAM_RECRUITMENT_CLOSED(HttpStatus.BAD_REQUEST, "TEAM4006", "팀원 모집이 마감되었습니다."),
    TEAM_ALREADY_JOINED_REQUEST(HttpStatus.BAD_REQUEST, "TEAM4007", "이미 가입을 요청 중인 팀입니다."),
    TEAM_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "TEAM4008", "이미 가입한 팀입니다."),
    TEAM_NOT_LEADER(HttpStatus.BAD_REQUEST, "TEAM4009", "팀 리더만 승인할 수 있습니다."),
    TEAM_NOT_REQUESTED(HttpStatus.BAD_REQUEST, "TEAM4010", "팀 가입을 신청한 사용자가 아닙니다."),
    TEAM_LEADER_CANNOT_EXIT(HttpStatus.BAD_REQUEST, "TEAM4011", "팀 리더는 팀을 탈퇴할 수 없습니다."),
    TEAM_NOT_JOINED(HttpStatus.BAD_REQUEST, "TEAM4012", "가입하지 않은 팀입니다."),
    TEAM_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4013", "팀 게시글을 찾을 수 없습니다."),
    TEAM_POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "TEAM4014", "팀 게시글을 생성할 권한이 없습니다."),
    TEAM_POST_UPDATE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "TEAM4015", "팀 게시글을 수정할 권한이 없습니다."),
    TEAM_POST_DELETE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "TEAM4016", "팀 게시글을 삭제할 권한이 없습니다."),
    TEAM_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4017", "팀 댓글을 찾을 수 없습니다."),
    TEAM_COMMENT_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TEAM4018", "댓글을 수정할 권한이 없습니다."),
    TEAM_COMMENT_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TEAM4019", "댓글을 삭제할 권한이 없습니다."),
    TEAM_REPLY_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM4020", "팀 대댓글을 찾을 수 없습니다."),
    TEAM_REPLY_COMMENT_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TEAM4021", "대댓글을 수정할 권한이 없습니다."),
    TEAM_REPLY_COMMENT_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TEAM4022", "대댓글을 삭제할 권한이 없습니다."),
    TEAM_NOT_PARTICIPANT(HttpStatus.BAD_REQUEST, "TEAM4023", "해당 팀원이 아닙니다."),

    USER_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4000", "사용자 통계를 찾을 수 없습니다."),
    DEPARTMENT_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4001", "학과 통계를 찾을 수 없습니다."),
    USER_DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4002", "사용자의 학과를 찾을 수 없습니다."),
    TOTAL_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4003", "전체 통계를 찾을 수 없습니다."),
    TOTAL_DEPARTMENT_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4004", "학과 전체 통계를 찾을 수 없습니다."),
    TOTAL_COLLEGE_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4005", "단과대 전체 통계를 찾을 수 없습니다."),
    USER_COUNT_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTICS4006", "사용자 수 통계를 찾을 수 없습니다."),
    INVALID_ACTION_TYPE(HttpStatus.BAD_REQUEST, "STATISTICS4007", "유효하지 않은 통계 타입입니다."),

    INVALID_APPLICATION_NUMBER(HttpStatus.BAD_REQUEST, "APPLICATION4000", "유효하지 않은 신청 번호입니다."),

    NOT_EXIST_PATENT(HttpStatus.BAD_REQUEST, "PATENT4000", "존재하지 않는 특허입니다."),
    INVALID_INVENTORS_SHARE(HttpStatus.BAD_REQUEST, "PATENT4001", "발명자의 점유율이 100%가 아닙니다."),

    PATENT_API_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PATENT5000", "특허 API와의 연결에 실패하였습니다."),
    PATENT_API_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PATENT5001", "특허 API URL이 잘못되었습니다."),
    PATENT_API_IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PATENT5002", "특허 API와의 통신 중 IO 에러가 발생하였습니다."),
    PATENT_API_PARSER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PATENT5003", "특허 API와의 통신 중 파싱 에러가 발생하였습니다."),
    PATENT_API_SAX_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PATENT5004", "특허 API와의 통신 중 SAX 에러가 발생하였습니다."),

    INVALID_SEARCH_QUERY(HttpStatus.BAD_REQUEST, "SEARCH4000", "검색어가 유효하지 않습니다."),
    INVALID_SEARCH_TYPE(HttpStatus.BAD_REQUEST, "SEARCH4001", "검색 타입이 유효하지 않습니다."),

    UTILITY_CLASS(HttpStatus.BAD_REQUEST, "COMMON4000", "유틸리티 클래스는 생성할 수 없습니다."),

    /**
     * 500 :  Database, Server 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5001", "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5002", "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5003", "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5004", "비밀번호 복호화에 실패하였습니다"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5005", "예상치 못한 에러가 발생했습니다."),
    FAILED_TO_RECEIVE_FRAME(HttpStatus.INTERNAL_SERVER_ERROR, "FRAME4000", "프레임을 받는데 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    /**
     * 에러 메시지와 코드를 포함하는 ErrorReasonDTO를 반환.
     *
     * @return 에러 메시지와 코드가 포함된 ErrorReasonDTO
     */
    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    /**
     * HTTP 상태와 에러 메시지, 코드를 포함하는 ErrorReasonDTO를 반환.
     *
     * @return HTTP 상태와 에러 메시지, 코드가 포함된 ErrorReasonDTO
     */
    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}

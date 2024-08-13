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
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON4003", "금지된 요청입니다."),
    RESPONSE_ERROR(HttpStatus.NOT_FOUND, "COMMON4004", "값을 불러오는데 실패하였습니다."),

    USERS_EMPTY_EMAIL( HttpStatus.BAD_REQUEST, "USER4000", "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(HttpStatus.BAD_REQUEST,"USER4001" , "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "USER4002","중복된 아이디입니다."),
    FAILED_TO_LOGIN(HttpStatus.BAD_REQUEST, "USER4003", "존재하지 않는 아이디거나 비밀번호가 틀렸습니다."),
    NOT_FIND_USER(HttpStatus.NOT_FOUND, "USER4000", "일치하는 유저가 없습니다."),
    EXIST_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER4005", "이미 존재하는 전화번호입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER4006", "핸드폰 번호 양식에 맞지 않습니다. 예시: +82-10-0000-0000"),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "USER4004", "이미 존재하는 사용자입니다."),
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "JWT4000", "JWT를 입력해주세요"),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "JWT4001", "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(HttpStatus.FORBIDDEN, "JWT4002", "권한이 없는 유저의 접근입니다."),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "JWT4003","인증 헤더가 없습니다."),

    KAKAO_TOKEN_RECEIVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN4000", "카카오 서버로부터 액세스 토큰을 받는데 실패했습니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4001", "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4002,", "유효하지 않은 토큰입니다."),
    INVALID_OAUTH_TYPE(HttpStatus.BAD_REQUEST, "OAUTH4000", "알 수 없는 소셜 로그인 형식입니다."),

    INVALID_PAGE(HttpStatus.BAD_REQUEST, "PAGE4000", "페이지는 0 이상이어야 합니다."),
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "PAGE4001", "사이즈는 0이상이어야 합니다."),

    JSON_CONVERT_ERROR(HttpStatus.BAD_REQUEST, "JSON4000", "JSON 변환에 실패하였습니다."),


    S3_UPLOAD(HttpStatus.BAD_REQUEST, "S3UPLOAD4001", "S3 파일 업로드 실패."),
    FAILED_TO_CONVERT_MULTIPARTFILE_RESOURCE(HttpStatus.BAD_REQUEST, "FILE4000", "MultipartFileResource 변환에 실패하였습니다."),
    FILE_CONVERT(HttpStatus.BAD_REQUEST, "FILE4001", "파일 변환 실패."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FILE4002", "파일을 찾을 수 없습니다."),

    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL4001", "이메일이 존재하지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "EMAIL4002", "이미 존재하는 이메일입니다."),
    EMAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "EMAIL4003", "이메일 전송에 실패했습니다."),
    EMAIL_AUTH_FAIL(HttpStatus.BAD_REQUEST, "EMAIL4004", "이메일 인증에 실패했습니다."),
    EMAIL_AUTH_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL4005", "이메일 인증 시간이 만료되었습니다."),
    EMAIL_AUTH_ALREADY(HttpStatus.BAD_REQUEST, "EMAIL4006", "이미 인증된 이메일입니다."),
    EMAIL_AUTH_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMAIL4007", "이메일 인증을 먼저 진행해주세요."),
    EMAIL_AUTH_NOT_MATCH(HttpStatus.BAD_REQUEST, "EMAIL4008", "이메일 인증번호가 일치하지 않습니다."),

    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DEPARTMENT4000", "학과를 찾을 수 없습니다."),
    /**
     * 500 :  Database, Server 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5001", "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5002", "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5003", "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5004", "비밀번호 복호화에 실패하였습니다"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5005", "예상치 못한 에러가 발생했습니다."),
    FAILED_TO_RECEIVE_FRAME(HttpStatus.INTERNAL_SERVER_ERROR, "FRAME4000", "프레임을 받는데 실패하였습니다."),
    FAILED_TO_CONNECT_FASTAPI_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "FASTAPI4000", "FastAPI 서버와의 연결에 실패하였습니다.");



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
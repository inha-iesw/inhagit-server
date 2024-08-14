package inha.git.common;

import inha.git.user.domain.enums.Role;

public class Constant {

    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String TOKEN_PREFIX = "Bearer ";

    public final static String EMAIL_TITLE = "[INHA GIT] 회원 가입 인증 이메일 입니다.";

    public final static String EMAIL_CONTENT = "[INHA GIT] 로그인 인증입니다." +
            "<br><br>" +
            "인증 번호는 <strong><i>%d</i></strong> 입니다." +
            "<br>" +
            "인증번호를 제대로 입력해주세요";


    public final static String SIGN_UP_TYPE = "1";
    public final static Integer STUDENT_TYPE = 1;
    public final static Integer ASSISTANT_TYPE = 2;
    public final static Integer PROFESSOR_TYPE = 3;
    public final static Integer COMPANY_TYPE = 4;

    public final static Integer ADMIN_TYPE = 1;
    public final static String CREATE_AT = "createAt";

    public static Integer mapRoleToPosition(Role role) {
        if(role == Role.USER) {
            return STUDENT_TYPE;  // 학생이 1인 경우
        }
        else if(role == Role.ASSISTANT) {
            return ASSISTANT_TYPE;  // 조교가 2인 경우
        }
        else if(role == Role.PROFESSOR) {
            return PROFESSOR_TYPE;  // 교수가 3인 경우
        }
        else if(role == Role.COMPANY) {
            return COMPANY_TYPE;  // 기업이 4인 경우
        }
        else if(role == Role.ADMIN) {
            return ADMIN_TYPE;  // 관리자가 1인 경우
        }
        else {
            return null;
        }
    }
}

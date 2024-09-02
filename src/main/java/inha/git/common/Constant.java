package inha.git.common;

import inha.git.user.domain.enums.Role;

public interface Constant {

    String HEADER_AUTHORIZATION = "Authorization";
    String TOKEN_PREFIX = "Bearer ";

    String EMAIL_TITLE = "[INHA GIT] 회원 가입 인증 이메일 입니다.";

    String EMAIL_CONTENT = "[INHA GIT] 로그인 인증입니다." +
            "<br><br>" +
            "인증 번호는 <strong><i>%d</i></strong> 입니다." +
            "<br>" +
            "인증번호를 제대로 입력해주세요";

    String SIGN_UP_TYPE = "1";
    Integer STUDENT_TYPE = 1;
    Integer ASSISTANT_TYPE = 2;
    Integer PROFESSOR_TYPE = 3;
    Integer COMPANY_TYPE = 4;
    Integer ADMIN_TYPE = 5;
    String CREATE_AT = "createAt";

    String BASE_DIR = System.getProperty("user.dir") + "/source/";
    String BASE_DIR_2 = System.getProperty("user.dir") + "/source";
    String PROJECT_ZIP = "project-zip";
    String PROJECT = "project";
    String ZIP = ".zip";
    String EVIDENCE = "evidence";
    String BANNER = "banner";
    String PROJECT_UPLOAD = "/project/";
    String PROBLEM_FILE = "problem-file";

    String GIT = ".git";
    String GITHUB = "https://github.com/";
    String DS_STORE = ".DS_Store";
    String DIRECTORY = "directory";
    String FILE = "file";
    String UNDERBAR = "._";
    String MACOSX = "__MACOSX";

    String SEARCH_PATENT = "?applicationNumber=";
    String ACCESS_KEY = "&accessKey=";
    String SERVICE_KEY = "&ServiceKey=";

    static Integer mapRoleToPosition(Role role) {
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
            return ADMIN_TYPE;  // 관리자가 5인 경우
        }
        else {
            return null;
        }
    }
}
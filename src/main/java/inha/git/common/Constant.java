package inha.git.common;

import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constant {

    @Value("${user.basedir}")
    private String basedirValue;
    public static String basedir;
    public static String BASE_DIR_SOURCE;
    public static String BASE_DIR_SOURCE_2;
    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String TOKEN_PREFIX = "Bearer ";
    public final static String TOKEN = "token ";
    public final static String EMAIL_TITLE = "[INHA - OSS] 회원 가입 인증 이메일 입니다.";
    public final static String EMAIL_CONTENT = "[INHA - OSS] 로그인 인증입니다." +
            "<br><br>" +
            "인증 번호는 <strong><i>%d</i></strong> 입니다." +
            "<br>" +
            "인증번호를 제대로 입력해주세요";
    public final static String FIND_PASSWORD_TITLE = "[INHA - OSS] 비밀번호 찾기 인증 이메일 입니다.";
    public final static String FIND_PASSWORD_CONTENT = "[INHA - OSS] 비밀번호 찾기 인증입니다." +
            "<br><br>" +
            "인증 번호는 <strong><i>%d</i></strong> 입니다." +
            "<br>" +
            "인증번호를 제대로 입력해주세요";
    public final static Integer MAX_FAILED_ATTEMPTS = 5;
    public final static String LOCKED = "locked";
    public final static String STUDENT_SIGN_UP_TYPE = "1";
    public final static String PROFESSOR_SIGN_UP_TYPE = "3";
    public final static String COMPANY_SIGN_UP_TYPE = "4";
    public final static Integer STUDENT_TYPE = 1;
    public final static Integer ASSISTANT_TYPE = 2;
    public final static Integer PROFESSOR_TYPE = 3;
    public final static Integer COMPANY_TYPE = 4;
    public final static Integer ADMIN_TYPE = 5;
    public final static Integer PASSWORD_TYPE = 9;
    public final static String CREATE_AT = "createAt";
    public final static String PROJECT_ZIP = "project-zip";
    public final static String PROJECT = "project";
    public final static String PROBLEM = "problem";
    public final static String PROBLEM_ZIP = "problem-zip";
    public final static String ZIP = ".zip";
    public final static String EVIDENCE = "evidence";
    public final static String BANNER = "banner";
    public final static String PROJECT_UPLOAD = "/project/";
    public final static String PROBLEM_FILE = "problem-file";
    public final static String GIT = ".git";
    public final static String PYC = ".pyc";
    public final static String DS_STORE = ".DS_Store";
    public final static String UNDERBAR = "._";
    public final static String MACOSX = "__MACOSX";
    public final static String PYCACHE = "__pycache__";
    public final static String IDEA = ".idea";
    public final static String DIR = "dir";
    public final static String ACCESS_KEY = "&accessKey=";
    public final static String SERVICE_KEY = "&ServiceKey=";
    public final static String SEARCH_PATENT = "?applicationNumber=";
    public final static String GITHUB_REPO_CACHE_PREFIX = "github:repos:";
    public final static String GITHUB_FILE_CACHE_PREFIX = "github:files:";
    public final static String GITHUB_FILE_CONTENT_CACHE_PREFIX = "github:fileContent:";
    public final static String GITHUB_API_URL = "https://api.github.com/repos/";
    public final static String GITHUB_URL = "https://github.com/";
    public final static String GITHUB_CONTENTS = "/contents/";
    public final static String NODE_MODULES = "node_modules";
    public final static String OUT = ".out";
    public final static String OUT_ = "out";
    public final static String IML = ".iml";
    public final static String FILE = "file";
    public final static String DIRECTORY = "directory";
    public final static String DSYM = ".dSYM";
    public final static String GRADLE = ".gradle";
    public final static String BUILD = "build";
    public final static String BAT = ".bat";
    public final static String CLASS = ".class";
    public final static String IDEMPOTENT = "IDEMPOTENT";
    public final static Integer TIME_LIMIT = 3;
    public final static String FIELD = "field";
    public final static String CURRICULUM = "교과";
    public final static String IMAGE = "image";
    public final static String ATTACHMENT = "attachment";
    public final static String OSS_PROJECT_URL = "https://oss.inha.ac.kr/project/detail/";
    public final static String I_FOSS_STATISTICS = "I-FOSS-통계";

    @PostConstruct
    public void init() {
        basedir = basedirValue;
        BASE_DIR_SOURCE = basedir + "/source/";
        BASE_DIR_SOURCE_2 = basedir + "/source";
    }

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
            return ADMIN_TYPE;  // 관리자가 5인 경우
        }
        else {
            return null;
        }
    }

    public static boolean hasAccessToProject(Project project, User user) {
        return project.getIsPublic() ||
                project.getUser().getId().equals(user.getId()) ||
                user.getRole().equals(Role.ADMIN) ||
                user.getRole().equals(Role.PROFESSOR);
    }
}

package inha.git.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Permission 열거형은 사용자 권한을 정의.
 * 각 권한은 특정 작업에 대한 액세스 권한을 나타냄.
 */
@RequiredArgsConstructor
public enum Permission {

    //관리자
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    //조교
    ASSISTANT_READ("assistant:read"),
    ASSISTANT_UPDATE("assistant:update"),
    ASSISTANT_CREATE("assistant:create"),
    ASSISTANT_DELETE("assistant:delete"),
    //교수
    PROFESSOR_READ("professor:read"),
    PROFESSOR_UPDATE("professor:update"),
    PROFESSOR_CREATE("professor:create"),
    PROFESSOR_DELETE("professor:delete"),
    //기업
    COMPANY_READ("company:read"),
    COMPANY_UPDATE("company:update"),
    COMPANY_CREATE("company:create"),
    COMPANY_DELETE("company:delete");

    @Getter
    private final String permission;
}

package inha.git.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static inha.git.user.domain.enums.Permission.*;


/**
 * Role 열거형은 사용자 역할을 정의.
 * 각 역할은 해당 역할에 부여된 권한 세트를 가짐.
 */
@RequiredArgsConstructor
@Getter
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    ASSISTANT_READ,
                    ASSISTANT_UPDATE,
                    ASSISTANT_DELETE,
                    ASSISTANT_CREATE,
                    PROFESSOR_READ,
                    PROFESSOR_UPDATE,
                    PROFESSOR_DELETE,
                    PROFESSOR_CREATE,
                    COMPANY_READ,
                    COMPANY_UPDATE,
                    COMPANY_DELETE,
                    COMPANY_CREATE
            )
    ),
    //조교
    ASSISTANT(
            Set.of(
                    ASSISTANT_READ,
                    ASSISTANT_UPDATE,
                    ASSISTANT_DELETE,
                    ASSISTANT_CREATE
            )
    ),
    //교수
    PROFESSOR(
            Set.of(
                    PROFESSOR_READ,
                    PROFESSOR_UPDATE,
                    PROFESSOR_DELETE,
                    PROFESSOR_CREATE,
                    ASSISTANT_READ,
                    ASSISTANT_UPDATE,
                    ASSISTANT_DELETE,
                    ASSISTANT_CREATE
            )
    ),
    //기업
    COMPANY(
            Set.of(
                    COMPANY_READ,
                    COMPANY_UPDATE,
                    COMPANY_DELETE,
                    COMPANY_CREATE
            )
    )
    ;

    /**
     * 해당 역할에 부여된 권한 세트.
     */
    @Getter
    private final Set<Permission> permissions;

    /**
     * 역할에 해당하는 권한 리스트를 반환.
     *
     * <p>권한은 SimpleGrantedAuthority 객체로 변환.</p>
     *
     * @return 역할에 해당하는 권한 리스트
     */
    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
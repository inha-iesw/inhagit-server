package inha.git.utils;

import inha.git.user.domain.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


/**
 * ApplicationAuditAware는 스프링 데이터 JPA의 감사 기능을 위한 현재 사용자의 ID를 제공.
 */
public class ApplicationAuditAware implements AuditorAware<Integer> {

    /**
     * 현재 인증된 사용자의 ID를 반환.
     *
     * <p>인증되지 않은 사용자의 경우, 빈 Optional을 반환.</p>
     *
     * @return 현재 사용자의 ID를 포함하는 Optional 객체, 인증되지 않은 경우 빈 Optional
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken
        ) {
            return Optional.empty();
        }

        User dentistPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(dentistPrincipal.getId());
    }
}

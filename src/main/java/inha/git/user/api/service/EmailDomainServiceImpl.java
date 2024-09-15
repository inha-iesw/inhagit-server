package inha.git.user.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.EmailDomain;
import inha.git.user.domain.repository.EmailDomainJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static inha.git.common.code.status.ErrorStatus.INVALID_EMAIL_DOMAIN;

/**
 * EmailDomainServiceImpl은 이메일 도메인 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailDomainServiceImpl implements EmailDomainService {

    private final EmailDomainJpaRepository emailDomainJpaRepository;

    /**
     * 이메일 도메인 유효성 검사
     *
     * @param email    이메일
     * @param userType 사용자 타입
     */
    public void validateEmailDomain(String email, int userType) {
        String domain = email.substring(email.indexOf("@") + 1); // 이메일 도메인 추출
        Optional<EmailDomain> validDomain = emailDomainJpaRepository.findByUserTypeAndEmailDomain(userType, domain);
        if (validDomain.isEmpty()) {
            throw new BaseException(INVALID_EMAIL_DOMAIN);
        }
    }

}

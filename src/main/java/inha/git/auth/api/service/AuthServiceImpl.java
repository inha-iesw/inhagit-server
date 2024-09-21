package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.FindEmailRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.Company;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.TOKEN_PREFIX;
import static inha.git.common.code.status.ErrorStatus.*;


/**
 * AuthServiceImpl은 인증 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userJpaRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final JwtProvider jwtProvider;
    private final ProfessorJpaRepository professorJpaRepository;
    private final CompanyJpaRepository companyJpaRepository;

    /**
     * 로그인 API
     *
     * <p>로그인을 처리합니다.</p>
     *
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 결과를 포함하는 LoginResponse
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User findUser = userJpaRepository.findByEmailAndState(loginRequest.email(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        try{
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            log.error("로그인 실패 - 사용자: {}", loginRequest.email());
            throw new BaseException(NOT_FIND_USER);
        }
        if(findUser.getBlockedAt() != null) {
            log.error("차단된 사용자 로그인 시도 - 사용자: {}", loginRequest.email());
            throw new BaseException(BLOCKED_USER);
        }
        Role role = findUser.getRole();
        if(role == Role.PROFESSOR) {
            Professor professor = professorJpaRepository.findByUserId(findUser.getId())
                    .orElseThrow(() -> new BaseException(NOT_FIND_USER));
            if(professor.getAcceptedAt() == null) {
                log.error("승인되지 않은 교수 로그인 시도 - 사용자: {}", loginRequest.email());
                throw new BaseException(NOT_APPROVED_USER);
            }
        }
        else if(role == Role.COMPANY) {
            Company company = companyJpaRepository.findByUserId(findUser.getId())
                    .orElseThrow(() -> new BaseException(NOT_FIND_USER));
            if(company.getAcceptedAt() == null) {
                log.error("승인되지 않은 기업 로그인 시도 - 사용자: {}", loginRequest.email());
                throw new BaseException(NOT_APPROVED_USER);
            }
        }
        String accessToken = jwtProvider.generateToken(findUser);
        log.info("사용자 {} 로그인 성공", findUser.getEmail());
        return authMapper.userToLoginResponse(findUser, TOKEN_PREFIX + accessToken);
    }

    /**
     * 이메일 찾기 API
     *
     * <p>이메일을 찾습니다.</p>
     *
     * @param findEmailRequest 이메일 찾기 요청 정보
     * @return 이메일을 포함하는 FindEmailResponse
     */
    @Override
    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest) {
        User user = userJpaRepository.findByUserNumberAndName(findEmailRequest.userNumber(), findEmailRequest.name())
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        log.info("사용자 {} 이메일 찾기 성공", user.getName());
        return authMapper.userToFindEmailResponse(user);
    }
}


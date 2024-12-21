package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.ChangePasswordRequest;
import inha.git.auth.api.controller.dto.request.FindEmailRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.response.UserResponse;
import inha.git.user.domain.Company;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import inha.git.utils.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
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
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RedisProvider redisProvider;


    /**
     * 사용자 로그인을 처리하는 서비스입니다.
     *
     * <p>
     * 로그인 과정:
     * 1. 이메일로 사용자 조회
     * 2. 계정 잠금 상태 확인
     * 3. 비밀번호 검증
     *    - 실패 시 실패 횟수 증가
     *    - 최대 실패 횟수 초과 시 계정 잠금
     * 4. 차단된 사용자 확인
     * 5. 교수/기업 회원의 경우 승인 여부 확인
     * 6. JWT 토큰 발급
     * </p>
     *
     * @param loginRequest 이메일과 비밀번호를 포함한 로그인 요청 정보
     * @return LoginResponse JWT 토큰과 사용자 정보를 포함한 로그인 응답
     * @throws BaseException 다음의 경우에 발생:
     *      - NOT_FIND_USER: 존재하지 않는 이메일이거나 비밀번호가 일치하지 않는 경우
     *      - ACCOUNT_LOCKED: 계정이 잠금 상태이거나 로그인 실패 횟수 초과로 잠긴 경우
     *      - BLOCKED_USER: 관리자에 의해 차단된 사용자인 경우
     *      - NOT_APPROVED_USER: 승인되지 않은 교수/기업 회원인 경우
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User findUser = userJpaRepository.findByEmailAndState(loginRequest.email(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        String lockoutKey = "lockout:" + findUser.getEmail();
        if (redisProvider.getValueOps(lockoutKey) != null) {
            log.error("계정이 잠긴 사용자 로그인 시도 - 사용자: {}", loginRequest.email());
            throw new BaseException(ACCOUNT_LOCKED);
        }
        String failedAttemptsKey = "failedAttempts:" + findUser.getEmail();
        Integer failedAttempts = redisProvider.getValueOps(failedAttemptsKey) != null
                ? Integer.parseInt(redisProvider.getValueOps(failedAttemptsKey))
                : 0;

        try{
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            failedAttempts++;
            redisProvider.setDataExpire(failedAttemptsKey, failedAttempts.toString(), 3600);

            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                redisProvider.setDataExpire(lockoutKey, LOCKED, 600);
                redisProvider.deleteValueOps(failedAttemptsKey);
                log.error("사용자 {} 계정이 잠겼습니다.", loginRequest.email());
                throw new BaseException(ACCOUNT_LOCKED);
            }
            log.error("로그인 실패 - 사용자: {}", loginRequest.email());
            throw new BaseException(NOT_FIND_USER);
        }
        if(findUser.getBlockedAt() != null) {
            log.error("차단된 사용자 로그인 시도 - 사용자: {}", loginRequest.email());
            throw new BaseException(BLOCKED_USER);
        }
        redisProvider.deleteValueOps(failedAttemptsKey);

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
     * 학번과 이름으로 사용자의 이메일을 찾는 서비스입니다.
     *
     * <p>
     * 사용자의 학번과 이름을 받아서:
     * 1. 해당하는 사용자가 존재하는지 확인
     * 2. 존재하는 경우 사용자의 이메일 정보를 반환
     * 3. 존재하지 않는 경우 예외 발생
     * </p>
     *
     * @param findEmailRequest 학번과 이름이 포함된 이메일 찾기 요청 정보
     * @return FindEmailResponse 찾은 사용자의 이메일 정보
     * @throws BaseException NOT_FIND_USER - 해당하는 학번과 이름을 가진 사용자가 존재하지 않는 경우
     */
    @Override
    public FindEmailResponse findEmail(FindEmailRequest findEmailRequest) {
        User user = userJpaRepository.findByUserNumberAndName(findEmailRequest.userNumber(), findEmailRequest.name())
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        log.info("사용자 {} 이메일 찾기 성공", user.getName());
        return authMapper.userToFindEmailResponse(user);
    }

    /**
     * 비밀번호 찾기 후 새로운 비밀번호로 변경하는 서비스입니다.
     *
     * <p>
     * 처리 과정:
     * 1. 이메일 인증 상태 확인
     * 2. 사용자 존재 여부 확인
     * 3. 새로운 비밀번호 암호화
     * 4. 비밀번호 업데이트
     * </p>
     *
     * @param changePasswordRequest 이메일과 새로운 비밀번호가 포함된 요청
     * @return UserResponse 비밀번호가 변경된 사용자의 정보
     * @throws BaseException EMAIL_AUTH_NOT_FOUND: 이메일 인증이 완료되지 않은 경우,
     *                      NOT_FIND_USER: 존재하지 않는 이메일이거나 활성 상태가 아닌 경우
     */
    @Override
    public UserResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        mailService.emailAuth(changePasswordRequest.email(), PASSWORD_TYPE.toString());
        User user = userJpaRepository.findByEmailAndState(changePasswordRequest.email(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        user.setPassword(passwordEncoder.encode(changePasswordRequest.pw()));
        log.info("비밀번호 변경 성공 - 이메일: {}", user.getEmail());
        return authMapper.userToUserResponse(user);
    }
}


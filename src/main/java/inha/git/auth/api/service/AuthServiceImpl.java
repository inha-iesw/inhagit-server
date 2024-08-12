package inha.git.auth.api.service;

import dentlix.server.auth.api.controller.dto.request.LoginRequest;
import dentlix.server.auth.api.controller.dto.request.SignupRequest;
import dentlix.server.auth.api.controller.dto.response.LoginResponse;
import dentlix.server.auth.api.controller.dto.response.RefreshResponse;
import dentlix.server.auth.api.controller.dto.response.SignupResponse;
import dentlix.server.auth.api.mapper.AuthMapper;
import dentlix.server.common.exceptions.BaseException;
import dentlix.server.user.domain.User;
import dentlix.server.user.domain.repository.UserJpaRepository;
import dentlix.server.utils.RedisProvider;
import dentlix.server.utils.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static dentlix.server.common.BaseEntity.State.ACTIVE;
import static dentlix.server.common.code.status.ErrorStatus.*;

/**
 * AuthServiceImpl은 인증 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;

    @Value("${fastapi.server.url}")
    private String fastAPIServerUrl;
    /**
     * fastAPI 서버 상태 확인
     *
     * @return fastAPI 서버 상태 메시지
     */
    @Override
    public String fastAPIHealth() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(fastAPIServerUrl, String.class);

            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : "FastAPI server is not healthy";
        } catch (RestClientException e) {
            throw new BaseException(FAILED_TO_CONNECT_FASTAPI_SERVER);
        }
    }

    /**
     * 회원 가입
     *
     * <p>회원 가입 요청 정보를 받아 회원 가입을 처리하고, 회원 가입 응답 정보를 반환한다.</p>
     *
     * @param signupRequest 회원 가입 요청 정보
     * @return 회원 가입 응답 정보
     */
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if (userJpaRepository.existsByUsername(signupRequest.username())) {
            throw new BaseException(ALREADY_EXIST_USER);
        }
        User user = authMapper.signupRequestToUser(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.password())); // 비밀번호 인코딩
        User savedUser = userJpaRepository.save(user);
        String accessToken = jwtProvider.generateToken(savedUser);
        String refreshToken = jwtProvider.generateRefreshToken(savedUser);
        saveUserToken(savedUser, refreshToken);
        return authMapper.userToSignupResponse(savedUser, accessToken, refreshToken);
    }

    /**
     * 로그인
     *
     * <p>로그인 요청 정보를 받아 로그인을 처리하고, 로그인 응답 정보를 반환한다.</p>
     *
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 응답 정보
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User findUser = userJpaRepository.findByUsernameAndState(loginRequest.username(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        try{
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            throw new BaseException(FAILED_TO_LOGIN);
        }
        String accessToken = jwtProvider.generateToken(findUser);
        String refreshToken = jwtProvider.generateRefreshToken(findUser);
        revokeAllUserTokens(findUser);
        saveUserToken(findUser, refreshToken);
        return authMapper.userToLoginResponse(findUser, accessToken, refreshToken);
    }

    /**
     * 리프레시 토큰 재발급
     *
     * <p>리프레시 토큰을 이용해 새로운 엑세스 토큰을 발급한다.</p>
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 새로운 엑세스 토큰을 포함하는 RefreshResponse
     */
    @Override
    @Transactional
    public RefreshResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        // Authorization 헤더가 없거나 올바른 형식이 아닌 경우 처리
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BaseException(MISSING_AUTH_HEADER);
        }
        final String refreshToken = authHeader.substring(7);
        final String username = jwtProvider.extractUsername(refreshToken);
        // 토큰에서 username을 추출할 수 없는 경우 처리
        if (username == null) {
            throw new BaseException(INVALID_TOKEN);
        }
        User user = userJpaRepository.findByUsernameAndState(username, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        // 토큰이 유효하지 않은 경우 처리
        if (!jwtProvider.isTokenValid(refreshToken, user)) {
            throw new BaseException(INVALID_TOKEN);
        }
        // Redis에 저장된 refreshToken이 요청의 refreshToken과 일치하지 않는 경우 처리
        String storedRefreshToken = redisProvider.getValueOps(user.getUsername());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new BaseException(INVALID_TOKEN);
        }
        // 새로운 엑세스 토큰 생성 및 반환
        String newAccessToken = jwtProvider.generateToken(user);
        return authMapper.accessTokenToRefreshResponse(newAccessToken);
    }
    /**
     * 사용자 토큰 저장
     *
     * @param user 사용자 정보
     * @param refreshToken 리프레시 토큰
     */
    private void saveUserToken(User user, String refreshToken) {
        redisProvider.setValueOps(user.getUsername(), refreshToken);
        redisProvider.expireValues(user.getUsername());
    }

    /**
     * 사용자의 모든 토큰 폐기
     *
     * @param user 사용자 정보
     */
    private void revokeAllUserTokens(User user) {
        redisProvider.deleteValueOps(user.getUsername());
    }
}


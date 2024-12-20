package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import inha.git.utils.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;  // JUnit5 import로 변경
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.TOKEN_PREFIX;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ProfessorJpaRepository professorJpaRepository;

    @Mock
    private CompanyJpaRepository companyJpaRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private RedisProvider redisProvider;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("학생 로그인 성공")
        void login_Success() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.USER);
            String accessToken = "test.access.token";
            LoginResponse expectedResponse = createLoginResponse(user, accessToken);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null); // 잠금 및 실패 횟수 없음
            given(jwtProvider.generateToken(user))
                    .willReturn(accessToken);
            given(authMapper.userToLoginResponse(user, TOKEN_PREFIX + accessToken))
                    .willReturn(expectedResponse);

            // when
            LoginResponse response = authService.login(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(authenticationManager).authenticate(any());
        }

        @Test
        @DisplayName("교수 로그인 성공")
        void login_Professor_Success() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.PROFESSOR);
            Professor professor = createApprovedProfessor(user);
            String accessToken = "test.access.token";
            LoginResponse expectedResponse = createLoginResponse(user, accessToken);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null);
            given(professorJpaRepository.findByUserId(user.getId()))
                    .willReturn(Optional.of(professor));
            given(jwtProvider.generateToken(user))
                    .willReturn(accessToken);
            given(authMapper.userToLoginResponse(user, TOKEN_PREFIX + accessToken))
                    .willReturn(expectedResponse);

            // when
            LoginResponse response = authService.login(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(authenticationManager).authenticate(any());
        }

        @Test
        @DisplayName("승인되지 않은 교수 로그인 실패")
        void login_NotApprovedProfessor_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.PROFESSOR);
            Professor professor = createNotApprovedProfessor(user);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null);
            given(professorJpaRepository.findByUserId(user.getId()))
                    .willReturn(Optional.of(professor));

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(BaseException.class, () -> authService.login(request))
                    .getErrorReason()
                    .equals(NOT_APPROVED_USER);
        }

        @Test
        @DisplayName("계정 잠김 상태로 로그인 시도")
        void login_AccountLocked_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.USER);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps("lockout:" + request.email()))
                    .willReturn("LOCKED");

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(BaseException.class, () -> authService.login(request))
                    .getErrorReason()
                    .equals(ACCOUNT_LOCKED);
        }

        @Test
        @DisplayName("차단된 사용자 로그인 시도")
        void login_BlockedUser_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createBlockedUser();

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null);

            // when & then
            org.junit.jupiter.api.Assertions.assertThrows(BaseException.class, () -> authService.login(request))
                    .getErrorReason()
                    .equals(BLOCKED_USER);
        }

        private LoginRequest createLoginRequest() {
            return new LoginRequest("test@test.com", "password123!");
        }

        private User createUser(Role role) {
            return User.builder()
                    .id(1)
                    .email("test@test.com")
                    .pw("encodedPassword")
                    .role(role)
                    .build();
        }

        private User createBlockedUser() {
            return User.builder()
                    .id(1)
                    .email("test@test.com")
                    .pw("encodedPassword")
                    .blockedAt(LocalDateTime.now())
                    .build();
        }

        private Professor createApprovedProfessor(User user) {
            return Professor.builder()
                    .id(1)
                    .user(user)
                    .acceptedAt(LocalDateTime.now())
                    .build();
        }

        private Professor createNotApprovedProfessor(User user) {
            return Professor.builder()
                    .id(1)
                    .user(user)
                    .build();
        }

        private LoginResponse createLoginResponse(User user, String accessToken) {
            return new LoginResponse(
                    user.getId(),
                    TOKEN_PREFIX + accessToken
            );
        }
    }
}
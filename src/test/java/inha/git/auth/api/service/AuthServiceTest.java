package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.ChangePasswordRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@DisplayName("인증 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private MailService mailService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ProfessorJpaRepository professorJpaRepository;

    @Mock
    private CompanyJpaRepository companyJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
            assertThrows(BaseException.class, () -> authService.login(request))
                    .getErrorReason()
                    .equals(NOT_APPROVED_USER);
        }

        @Test
        @DisplayName("기업 회원 로그인 성공")
        void login_Company_Success() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.COMPANY);
            Company company = createTestCompany(user, true);  // 승인된 기업
            String accessToken = "test.access.token";
            LoginResponse expectedResponse = createLoginResponse(user, accessToken);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps("lockout:" + request.email()))
                    .willReturn(null);
            given(companyJpaRepository.findByUserId(user.getId()))
                    .willReturn(Optional.of(company));
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
        @DisplayName("승인되지 않은 기업 회원 로그인 실패")
        void login_NotApprovedCompany_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.COMPANY);
            Company company = createTestCompany(user, false);  // 승인되지 않은 기업

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps("lockout:" + request.email()))
                    .willReturn(null);
            given(companyJpaRepository.findByUserId(user.getId()))
                    .willReturn(Optional.of(company));

            // when & then
            BaseException exception = assertThrows(BaseException.class,
                    () -> authService.login(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(NOT_APPROVED_USER.getMessage());
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
            assertThrows(BaseException.class, () -> authService.login(request))
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
            assertThrows(BaseException.class, () -> authService.login(request))
                    .getErrorReason()
                    .equals(BLOCKED_USER);
        }

        @Test
        @DisplayName("비밀번호 실패 횟수 초과로 계정 잠금")
        void login_ExceedMaxFailedAttempts_AccountLocked() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.USER);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps("lockout:" + request.email()))
                    .willReturn(null);
            given(redisProvider.getValueOps("failedAttempts:" + request.email()))
                    .willReturn(String.valueOf(MAX_FAILED_ATTEMPTS - 1));
            doThrow(new BadCredentialsException("Invalid credentials"))
                    .when(authenticationManager)
                    .authenticate(any());

            // when & then
            BaseException exception = assertThrows(BaseException.class,
                    () -> authService.login(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(ACCOUNT_LOCKED.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시도")
        void login_NonExistentEmail_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class,
                    () -> authService.login(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(NOT_FIND_USER.getMessage());
        }

        @Test
        @DisplayName("Redis 작업 실패 시 예외 발생")
        void login_RedisOperationFails_ThrowsException() {
            // given
            LoginRequest request = createLoginRequest();
            User user = createUser(Role.USER);

            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willThrow(new RuntimeException("Redis connection failed"));

            // when & then
            assertThrows(RuntimeException.class,
                    () -> authService.login(request));
        }

        private Company createTestCompany(User user, boolean isApproved) {
            return Company.builder()
                    .id(1)
                    .user(user)
                    .affiliation("테스트기업")
                    .acceptedAt(isApproved ? LocalDateTime.now() : null)
                    .build();
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

    @Nested
    @DisplayName("비밀번호 변경 테스트")
    class ChangePasswordTest {

        @Test
        @DisplayName("이메일 인증 후 비밀번호 변경 성공")
        void changePassword_Success() {
            // given
            ChangePasswordRequest request = new ChangePasswordRequest(
                    "test@test.com",
                    "newPassword123!"
            );
            User user = createUser();
            UserResponse expectedResponse = new UserResponse(1);

            willDoNothing().given(mailService).emailAuth(anyString(), anyString());
            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.of(user));
            given(passwordEncoder.encode(request.pw()))
                    .willReturn("encodedPassword");
            given(authMapper.userToUserResponse(user))
                    .willReturn(expectedResponse);

            // when
            UserResponse response = authService.changePassword(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(mailService).emailAuth(request.email(), PASSWORD_TYPE.toString());
            verify(passwordEncoder).encode(request.pw());
            verify(userJpaRepository).findByEmailAndState(request.email(), ACTIVE);
        }

        @Test
        @DisplayName("이메일 인증되지 않은 경우 실패")
        void changePassword_NotAuthenticated_ThrowsException() {
            // given
            ChangePasswordRequest request = new ChangePasswordRequest(
                    "test@test.com",
                    "newPassword123!"
            );

            doThrow(new BaseException(EMAIL_AUTH_NOT_FOUND))
                    .when(mailService)
                    .emailAuth(anyString(), anyString());

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    authService.changePassword(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(EMAIL_AUTH_NOT_FOUND.getMessage());
            verify(userJpaRepository, never()).findByEmailAndState(anyString(), any());
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 변경 시도")
        void changePassword_UserNotFound_ThrowsException() {
            // given
            ChangePasswordRequest request = new ChangePasswordRequest(
                    "test@test.com",
                    "newPassword123!"
            );

            willDoNothing().given(mailService).emailAuth(anyString(), anyString());
            given(userJpaRepository.findByEmailAndState(request.email(), ACTIVE))
                    .willReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    authService.changePassword(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(NOT_FIND_USER.getMessage());
            verify(passwordEncoder, never()).encode(anyString());
        }

        private User createUser() {
            return User.builder()
                    .id(1)
                    .email("test@test.com")
                    .name("홍길동")
                    .build();
        }
    }

}
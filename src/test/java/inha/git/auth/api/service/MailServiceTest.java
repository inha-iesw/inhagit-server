package inha.git.auth.api.service;

import inha.git.auth.api.controller.dto.request.*;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.mapper.AuthMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.service.EmailDomainService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static inha.git.common.Constant.PASSWORD_TYPE;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.ErrorStatus.NOT_FIND_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {


    @InjectMocks
    private MailServiceImpl mailServiceImpl;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserJpaRepository userJpaRepository;


    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private EmailDomainService emailDomainService;

    @Mock
    private RedisProvider redisProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailServiceImpl, "username", "test@test.com");
    }

    @Nested
    @DisplayName("이메일 인증 테스트")
    class MailSendTest {

        @Test
        @DisplayName("이메일 인증번호 전송 성공")
        void mailSend_Success() {
            // given
            EmailRequest request = new EmailRequest("test@inha.edu", 1);
            MimeMessage mimeMessage = mock(MimeMessage.class);

            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null);
            given(javaMailSender.createMimeMessage())
                    .willReturn(mimeMessage);
            willDoNothing().given(javaMailSender).send(any(MimeMessage.class));
            willDoNothing().given(emailDomainService)
                    .validateEmailDomain(anyString(), anyInt());  // Mock 동작 추가

            // when
            String result = mailServiceImpl.mailSend(request);

            // then
            assertThat(result).isEqualTo("이메일 전송 완료");
            verify(javaMailSender).send(any(MimeMessage.class));
            verify(emailDomainService).validateEmailDomain(request.email(), request.type());
            verify(redisProvider).setDataExpire(
                    anyString(),
                    anyString(),
                    eq(60 * 3L)
            );
        }

        @Test
        @DisplayName("이메일 인증번호 확인 성공")
        void mailSendCheck_Success() {
            // given
            EmailCheckRequest request = new EmailCheckRequest("test@inha.edu", 1, "123456");

            given(redisProvider.getValueOps(anyString()))
                    .willReturn("123456");
            willDoNothing().given(emailDomainService)
                    .validateEmailDomain(anyString(), anyInt());  // Mock 동작 추가

            // when
            Boolean result = mailServiceImpl.mailSendCheck(request);

            // then
            assertThat(result).isTrue();
            verify(emailDomainService).validateEmailDomain(request.email(), request.type());
            verify(redisProvider).setDataExpire(
                    startsWith("verification-"),
                    anyString(),
                    eq(60 * 60L)
            );
        }

        @Test
        @DisplayName("잘못된 인증번호로 확인 시도")
        void mailSendCheck_InvalidAuthNumber_ThrowsException() {
            // given
            EmailCheckRequest request = new EmailCheckRequest("test@inha.edu", 1, "123456");

            given(redisProvider.getValueOps(anyString()))
                    .willReturn("654321");
            willDoNothing().given(emailDomainService)
                    .validateEmailDomain(anyString(), anyInt());  // Mock 동작 추가

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    mailServiceImpl.mailSendCheck(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(EMAIL_AUTH_NOT_MATCH.getMessage());
        }
    }

    @Nested
    @DisplayName("이메일 찾기 테스트")
    class FindEmailTest {

        @Test
        @DisplayName("유효한 학번과 이름으로 이메일 찾기 성공")
        void findEmail_ValidUserNumberAndName_Success() {
            // given
            FindEmailRequest request = createValidFindEmailRequest();
            User user = createUser();
            FindEmailResponse expectedResponse = new FindEmailResponse(user.getEmail());

            given(userJpaRepository.findByUserNumberAndName(request.userNumber(), request.name()))
                    .willReturn(Optional.of(user));
            given(authMapper.userToFindEmailResponse(user))
                    .willReturn(expectedResponse);

            // when
            FindEmailResponse response = authService.findEmail(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(userJpaRepository).findByUserNumberAndName(request.userNumber(), request.name());
            verify(authMapper).userToFindEmailResponse(user);
        }

        @Test
        @DisplayName("존재하지 않는 학번으로 조회시 예외 발생")
        void findEmail_InvalidUserNumber_ThrowsException() {
            // given
            FindEmailRequest request = createValidFindEmailRequest();

            given(userJpaRepository.findByUserNumberAndName(request.userNumber(), request.name()))
                    .willReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    authService.findEmail(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(NOT_FIND_USER.getMessage());
        }

        @Test
        @DisplayName("일치하지 않는 이름으로 조회시 예외 발생")
        void findEmail_InvalidName_ThrowsException() {
            // given
            FindEmailRequest request = new FindEmailRequest("12345678", "잘못된이름");

            given(userJpaRepository.findByUserNumberAndName(request.userNumber(), request.name()))
                    .willReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    authService.findEmail(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(NOT_FIND_USER.getMessage());
        }

        private FindEmailRequest createValidFindEmailRequest() {
            return new FindEmailRequest(
                    "12345678",  // 학번
                    "홍길동"    // 이름
            );
        }

        private User createUser() {
            return User.builder()
                    .id(1)
                    .email("test@inha.edu")
                    .name("홍길동")
                    .userNumber("12345678")
                    .role(Role.USER)
                    .build();
        }
    }

    @Nested
    @DisplayName("비밀번호 찾기 이메일 인증 테스트")
    class FindPasswordMailTest {

        @Test
        @DisplayName("비밀번호 찾기 이메일 전송 성공")
        void findPasswordMailSend_Success() {
            // given
            FindPasswordRequest request = new FindPasswordRequest("test@test.com");
            User user = createUser();
            MimeMessage mimeMessage = mock(MimeMessage.class);

            given(userJpaRepository.findByEmail(request.email()))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(anyString()))
                    .willReturn(null);
            given(javaMailSender.createMimeMessage())
                    .willReturn(mimeMessage);
            willDoNothing().given(javaMailSender).send(any(MimeMessage.class));

            // when
            String result = mailServiceImpl.findPasswordMailSend(request);

            // then
            assertThat(result).isEqualTo("이메일 전송 완료");
            verify(javaMailSender).send(any(MimeMessage.class));
            verify(redisProvider).setDataExpire(
                    anyString(),
                    anyString(),
                    eq(60 * 3L)
            );
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 비밀번호 찾기 시도")
        void findPasswordMailSend_EmailNotFound_ThrowsException() {
            // given
            FindPasswordRequest request = new FindPasswordRequest("invalid@test.com");

            given(userJpaRepository.findByEmail(request.email()))
                    .willReturn(Optional.empty());

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    mailServiceImpl.findPasswordMailSend(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(EMAIL_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비밀번호 찾기 인증번호 확인 성공")
        void findPasswordMailSendCheck_Success() {
            // given
            FindPasswordCheckRequest request = new FindPasswordCheckRequest(
                    "test@test.com",
                    "123456"
            );
            User user = createUser();

            given(userJpaRepository.findByEmail(request.email()))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(request.email() + "-" + PASSWORD_TYPE))
                    .willReturn("123456");

            // when
            Boolean result = mailServiceImpl.findPasswordMailSendCheck(request);

            // then
            assertThat(result).isTrue();
            verify(redisProvider).setDataExpire(
                    anyString(),
                    anyString(),
                    eq(60 * 60L)
            );
        }

        @Test
        @DisplayName("만료된 인증번호로 확인 시도")
        void findPasswordMailSendCheck_AuthExpired_ThrowsException() {
            // given
            FindPasswordCheckRequest request = new FindPasswordCheckRequest(
                    "test@test.com",
                    "123456"
            );
            User user = createUser();

            given(userJpaRepository.findByEmail(request.email()))
                    .willReturn(Optional.of(user));
            given(redisProvider.getValueOps(request.email() + "-" + PASSWORD_TYPE))
                    .willReturn(null);

            // when & then
            BaseException exception = assertThrows(BaseException.class, () ->
                    mailServiceImpl.findPasswordMailSendCheck(request));

            assertThat(exception.getErrorReason().getMessage())
                    .isEqualTo(EMAIL_AUTH_EXPIRED.getMessage());
        }

        private User createUser() {
            return User.builder()
                    .id(1)
                    .email("test@test.com")
                    .name("테스트유저")
                    .build();
        }
    }

}
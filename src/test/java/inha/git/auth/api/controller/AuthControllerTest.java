package inha.git.auth.api.controller;

import inha.git.auth.api.controller.dto.request.*;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.service.AuthService;
import inha.git.auth.api.service.MailService;
import inha.git.common.BaseResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private MailService mailService;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 성공")
        void login_Success() {
            // given
            LoginRequest request = createValidLoginRequest();
            LoginResponse expectedResponse = new LoginResponse(1, "Bearer test.token");

            given(authService.login(request))
                    .willReturn(expectedResponse);

            // when
            BaseResponse<LoginResponse> response = authController.login(request);

            // then
            assertThat(response.getResult()).isEqualTo(expectedResponse);
            verify(authService).login(request);
        }

        @Test
        @DisplayName("이메일 인증 성공")
        void mailSend_Success() {
            // given
            EmailRequest request = createValidEmailRequest();
            String expectedResponse = "이메일 전송 완료";

            given(mailService.mailSend(request))
                    .willReturn(expectedResponse);

            // when
            BaseResponse<String> response = authController.mailSend(request);

            // then
            assertThat(response.getResult()).isEqualTo(expectedResponse);
            verify(mailService).mailSend(request);
        }

        @Test
        @DisplayName("이메일 인증확인 성공")
        void mailSendCheck_Success() {
            // given
            EmailCheckRequest request = createValidEmailCheckRequest();
            given(mailService.mailSendCheck(request))
                    .willReturn(true);

            // when
            BaseResponse<Boolean> response = authController.mailSendCheck(request);

            // then
            assertThat(response.getResult()).isTrue();
            verify(mailService).mailSendCheck(request);
        }

        private LoginRequest createValidLoginRequest() {
            return new LoginRequest(
                    "test@test.com",
                    "password123!"
            );
        }

        private EmailRequest createValidEmailRequest() {
            return new EmailRequest(
                    "test@test.com",
                    1  // 인증 타입
            );
        }

        private EmailCheckRequest createValidEmailCheckRequest() {
            return new EmailCheckRequest(
                    "test@test.com",
                    1,
                    "123456"
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 찾기 테스트")
    class FindPasswordTest {

        @Test
        @DisplayName("비밀번호 찾기 이메일 발송 성공")
        void findPasswordMailSend_Success() {
            // given
            FindPasswordRequest request = createValidFindPasswordRequest();
            String expectedResponse = "이메일 전송 완료";

            given(mailService.findPasswordMailSend(request))
                    .willReturn(expectedResponse);

            // when
            BaseResponse<String> response = authController.findPasswordMailSend(request);

            // then
            assertThat(response.getResult()).isEqualTo(expectedResponse);
            verify(mailService).findPasswordMailSend(request);
        }

        @Test
        @DisplayName("비밀번호 찾기 이메일 인증 확인 성공")
        void findPasswordMailSendCheck_Success() {
            // given
            FindPasswordCheckRequest request = createValidFindPasswordCheckRequest();
            given(mailService.findPasswordMailSendCheck(request))
                    .willReturn(true);

            // when
            BaseResponse<Boolean> response = authController.findPasswordMailSendCheck(request);

            // then
            assertThat(response.getResult()).isTrue();
            verify(mailService).findPasswordMailSendCheck(request);
        }

        private FindPasswordRequest createValidFindPasswordRequest() {
            return new FindPasswordRequest(
                    "test@test.com"
            );
        }

        private FindPasswordCheckRequest createValidFindPasswordCheckRequest() {
            return new FindPasswordCheckRequest(
                    "test@test.com",
                    "123456"
            );
        }
    }
}
package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Professor;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.ProfessorJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static inha.git.common.Constant.PROFESSOR_SIGN_UP_TYPE;
import static inha.git.common.Constant.PROFESSOR_TYPE;
import static inha.git.common.code.status.ErrorStatus.EMAIL_AUTH_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.INVALID_EMAIL_DOMAIN;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorServiceImpl professorService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private ProfessorJpaRepository professorJpaRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailDomainService emailDomainService;

    @Mock
    private MailService mailService;

    @Nested
    @DisplayName("교수 회원가입 테스트")
    class ProfessorSignupTest {

        @Test
        @DisplayName("교수 회원가입 성공")
        void professorSignup_Success() {
            // given
            ProfessorSignupRequest request = createValidProfessorSignupRequest();
            User mockUser = createMockUser();
            Professor mockProfessor = createMockProfessor(mockUser);
            User savedMockUser = createMockUser();
            ProfessorSignupResponse expectedResponse = new ProfessorSignupResponse(1);

            given(userMapper.professorSignupRequestToUser(request))
                    .willReturn(mockUser);
            given(passwordEncoder.encode(request.pw()))
                    .willReturn("encodedPassword");
            given(userMapper.professorSignupRequestToProfessor(request))
                    .willReturn(mockProfessor);
            given(userJpaRepository.save(any(User.class)))
                    .willReturn(savedMockUser);
            given(userMapper.userToProfessorSignupResponse(savedMockUser))
                    .willReturn(expectedResponse);

            // when
            ProfessorSignupResponse response = professorService.professorSignup(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(emailDomainService).validateEmailDomain(request.email(), PROFESSOR_TYPE);
            verify(mailService).emailAuth(request.email(), PROFESSOR_SIGN_UP_TYPE);
            verify(professorJpaRepository).save(any(Professor.class));
            verify(userJpaRepository).save(any(User.class));
        }

        @Test
        @DisplayName("이메일 도메인 검증 실패시 예외 발생")
        void professorSignup_InvalidEmailDomain_ThrowsException() {
            // given
            ProfessorSignupRequest request = createValidProfessorSignupRequest();
            doThrow(new BaseException(INVALID_EMAIL_DOMAIN))
                    .when(emailDomainService)
                    .validateEmailDomain(request.email(), PROFESSOR_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    professorService.professorSignup(request));
            verify(professorJpaRepository, never()).save(any());
            verify(userJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("이메일 인증 실패시 예외 발생")
        void professorSignup_EmailAuthFail_ThrowsException() {
            // given
            ProfessorSignupRequest request = createValidProfessorSignupRequest();
            doThrow(new BaseException(EMAIL_AUTH_NOT_FOUND))
                    .when(mailService)
                    .emailAuth(request.email(), PROFESSOR_SIGN_UP_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    professorService.professorSignup(request));
            verify(professorJpaRepository, never()).save(any());
            verify(userJpaRepository, never()).save(any());
        }

        private ProfessorSignupRequest createValidProfessorSignupRequest() {
            return new ProfessorSignupRequest(
                    "professor@inha.ac.kr",
                    "홍길동",
                    "password2@",
                    "221121",
                    List.of(1)
            );
        }

        private User createMockUser() {
            return User.builder()
                    .id(1)
                    .email("professor@inha.ac.kr")
                    .name("홍길동")
                    .pw("encodedPassword")
                    .userNumber("221121")
                    .role(Role.PROFESSOR)
                    .build();
        }

        private Professor createMockProfessor(User user) {
            return Professor.builder()
                    .id(1)
                    .user(user)
                    .build();
        }
    }
}
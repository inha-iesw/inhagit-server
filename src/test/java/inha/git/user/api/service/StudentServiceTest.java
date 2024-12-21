package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
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

import static inha.git.common.Constant.STUDENT_SIGN_UP_TYPE;
import static inha.git.common.Constant.STUDENT_TYPE;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailDomainService emailDomainService;

    @Mock
    private MailService mailService;

    @Nested
    @DisplayName("학생 회원가입 테스트")
    class StudentSignupTest {

        @Test
        @DisplayName("학생 회원가입 성공")
        void studentSignup_Success() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            User mockUser = createMockUser();
            User savedMockUser = createMockUser();
            StudentSignupResponse expectedResponse = new StudentSignupResponse(1);

            given(userMapper.studentSignupRequestToUser(request))
                    .willReturn(mockUser);
            given(passwordEncoder.encode(request.pw()))
                    .willReturn("encodedPassword");
            given(userJpaRepository.save(any(User.class)))
                    .willReturn(savedMockUser);
            given(userMapper.userToStudentSignupResponse(savedMockUser))
                    .willReturn(expectedResponse);

            // when
            StudentSignupResponse response = studentService.studentSignup(request);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(emailDomainService).validateEmailDomain(request.email(), STUDENT_TYPE);
            verify(mailService).emailAuth(request.email(), STUDENT_SIGN_UP_TYPE);
            verify(userJpaRepository).save(any(User.class));
        }

        @Test
        @DisplayName("이메일 도메인 검증 실패시 예외 발생")
        void studentSignup_InvalidEmailDomain_ThrowsException() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            doThrow(new BaseException(INVALID_EMAIL_DOMAIN))
                    .when(emailDomainService)
                    .validateEmailDomain(request.email(), STUDENT_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    studentService.studentSignup(request));
            verify(userJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("이메일 인증 실패시 예외 발생")
        void studentSignup_EmailAuthFail_ThrowsException() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            doThrow(new BaseException(EMAIL_AUTH_NOT_FOUND))
                    .when(mailService)
                    .emailAuth(request.email(), STUDENT_SIGN_UP_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    studentService.studentSignup(request));
            verify(userJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("학과 정보 매핑 실패")
        void studentSignup_DepartmentMappingFail_ThrowsException() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            User mockUser = createMockUser();

            given(userMapper.studentSignupRequestToUser(request))
                    .willReturn(mockUser);
            doThrow(new BaseException(DEPARTMENT_NOT_FOUND))
                    .when(userMapper)
                    .mapDepartmentsToUser(any(), any(), any());

            // when & then
            assertThrows(BaseException.class, () ->
                    studentService.studentSignup(request));
            verify(userJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 이메일로 회원가입 시도")
        void studentSignup_DuplicateEmail_ThrowsException() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            doThrow(new BaseException(DUPLICATE_EMAIL))
                    .when(emailDomainService)
                    .validateEmailDomain(request.email(), STUDENT_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    studentService.studentSignup(request));
            verify(userJpaRepository, never()).save(any());
        }

        private StudentSignupRequest createValidStudentSignupRequest() {
            return new StudentSignupRequest(
                    "test@inha.edu",
                    "홍길동",
                    "password2@",
                    "12241234",
                    List.of(1)
            );
        }

        private User createMockUser() {
            return User.builder()
                    .id(1)
                    .email("test@inha.edu")
                    .name("홍길동")
                    .pw("encodedPassword")
                    .userNumber("12241234")
                    .role(Role.USER)
                    .build();
        }
    }
}
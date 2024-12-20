package inha.git.user.api.service;

import inha.git.auth.api.service.MailService;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import inha.git.user.api.mapper.UserMapper;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.CompanyJpaRepository;
import inha.git.user.domain.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.Constant.COMPANY_SIGN_UP_TYPE;
import static inha.git.common.code.status.ErrorStatus.EMAIL_AUTH_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private CompanyJpaRepository companyJpaRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    @Nested
    @DisplayName("기업 회원가입 테스트")
    class CompanySignupTest {

        @org.junit.Test
        @DisplayName("기업 회원가입 성공")
        void companySignup_Success() {
            // given
            CompanySignupRequest request = createValidCompanySignupRequest();
            MultipartFile evidence = createMockMultipartFile();
            User mockUser = createMockUser();
            User savedMockUser = createMockUser();
            Company mockCompany = createMockCompany(mockUser);
            CompanySignupResponse expectedResponse = new CompanySignupResponse(1);

            given(userMapper.companySignupRequestToUser(request))
                    .willReturn(mockUser);
            given(passwordEncoder.encode(request.pw()))
                    .willReturn("encodedPassword");
            given(userJpaRepository.save(any(User.class)))
                    .willReturn(savedMockUser);
            given(userMapper.companySignupRequestToCompany(eq(request), anyString()))
                    .willReturn(mockCompany);
            given(userMapper.userToCompanySignupResponse(savedMockUser))
                    .willReturn(expectedResponse);

            // when
            CompanySignupResponse response = companyService.companySignup(request, evidence);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(mailService).emailAuth(request.email(), COMPANY_SIGN_UP_TYPE);
            verify(userJpaRepository).save(any(User.class));
            verify(companyJpaRepository).save(any(Company.class));
        }

        @Test
        @DisplayName("이메일 인증 실패시 예외 발생")
        void companySignup_EmailAuthFail_ThrowsException() {
            // given
            CompanySignupRequest request = createValidCompanySignupRequest();
            MultipartFile evidence = createMockMultipartFile();

            doThrow(new BaseException(EMAIL_AUTH_NOT_FOUND))
                    .when(mailService)
                    .emailAuth(request.email(), COMPANY_SIGN_UP_TYPE);

            // when & then
            assertThrows(BaseException.class, () ->
                    companyService.companySignup(request, evidence));
            verify(userJpaRepository, never()).save(any());
            verify(companyJpaRepository, never()).save(any());
        }

        private CompanySignupRequest createValidCompanySignupRequest() {
            return new CompanySignupRequest(
                    "company@example.com",
                    "홍길동",
                    "password2@",
                    "인하대학교"
            );
        }

        private MultipartFile createMockMultipartFile() {
            return new MockMultipartFile(
                    "evidence",
                    "evidence.pdf",
                    MediaType.APPLICATION_PDF_VALUE,
                    "test".getBytes()
            );
        }

        private User createMockUser() {
            return User.builder()
                    .id(1)
                    .email("company@example.com")
                    .name("홍길동")
                    .pw("encodedPassword")
                    .role(Role.COMPANY)
                    .build();
        }

        private Company createMockCompany(User user) {
            return Company.builder()
                    .id(1)
                    .user(user)
                    .affiliation("인하대학교")
                    .evidenceFilePath("/path/to/evidence.pdf")
                    .build();
        }
    }
}
package inha.git.user.api.controller;

import inha.git.common.BaseResponse;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.service.ProfessorService;
import inha.git.user.api.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private StudentService studentService;

    @Mock
    private ProfessorService professorService;

    @Nested
    @DisplayName("학생 회원가입 테스트")
    class StudentSignupTest {

        @Test
        @DisplayName("학생 회원가입 성공")
        void studentSignup_Success() {
            // given
            StudentSignupRequest request = createValidStudentSignupRequest();
            StudentSignupResponse expectedResponse = new StudentSignupResponse(1);
            given(studentService.studentSignup(request))
                    .willReturn(expectedResponse);

            // when
            BaseResponse<StudentSignupResponse> response =
                    userController.studentSignup(request);

            // then
            assertThat(response.getResult()).isEqualTo(expectedResponse);
            verify(studentService).studentSignup(request);
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
    }

    @Nested
    @DisplayName("교수 회원가입 테스트")
    class ProfessorSignupTest {
        @Test
        @DisplayName("교수 회원가입 성공")
        void professorSignup_Success() {
            // given
            ProfessorSignupRequest request = createValidProfessorSignupRequest();
            ProfessorSignupResponse expectedResponse = new ProfessorSignupResponse(1);
            given(professorService.professorSignup(request))
                    .willReturn(expectedResponse);

            // when
            BaseResponse<ProfessorSignupResponse> response = userController.professorSignup(request);

            // then
            assertThat(response.getResult()).isEqualTo(expectedResponse);
            verify(professorService).professorSignup(request);
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
    }
}
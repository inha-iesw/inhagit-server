package inha.git.semester.api.controller;

import inha.git.common.BaseResponse;
import inha.git.semester.controller.SemesterController;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.service.SemesterService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("학기 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class SemesterControllerTest {

    @InjectMocks
    private SemesterController semesterController;

    @Mock
    private SemesterService semesterService;

    @Test
    @DisplayName("학기 전체 조회 성공")
    void getSemesters_Success() {
        // given
        List<SearchSemesterResponse> expectedResponses = Arrays.asList(
                new SearchSemesterResponse(1, "2023-1"),
                new SearchSemesterResponse(2, "2023-2")
        );

        given(semesterService.getSemesters())
                .willReturn(expectedResponses);

        // when
        BaseResponse<List<SearchSemesterResponse>> response = semesterController.getSemesters();

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponses);
        verify(semesterService).getSemesters();
    }

    @Test
    @DisplayName("학기 생성 성공")
    void createSemester_Success() {
        // given
        User admin = createAdminUser();
        CreateSemesterRequest request = new CreateSemesterRequest("2024-1");
        String expectedResponse = "2024-1 학기가 생성되었습니다.";

        given(semesterService.createSemester(admin, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = semesterController.createSemester(admin, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(semesterService).createSemester(admin, request);
    }

    @Test
    @DisplayName("학기명 수정 성공")
    void updateSemester_Success() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 1;
        UpdateSemesterRequest request = new UpdateSemesterRequest("2024-2");
        String expectedResponse = "2024-2 학기 이름이 수정되었습니다.";

        given(semesterService.updateSemesterName(admin, semesterIdx, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = semesterController.updateSemester(admin, semesterIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(semesterService).updateSemesterName(admin, semesterIdx, request);
    }

    @Test
    @DisplayName("학기 삭제 성공")
    void deleteSemester_Success() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 1;
        String expectedResponse = "2024-1 학기가 삭제되었습니다.";

        given(semesterService.deleteSemester(admin, semesterIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = semesterController.deleteSemester(admin, semesterIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(semesterService).deleteSemester(admin, semesterIdx);
    }

    private User createAdminUser() {
        return User.builder()
                .id(1)
                .email("admin@test.com")
                .name("관리자")
                .role(Role.ADMIN)
                .build();
    }
}
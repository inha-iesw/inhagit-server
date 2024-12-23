package inha.git.department.api.controller;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.BaseResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.department.api.service.DepartmentService;
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

@DisplayName("학과 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @InjectMocks
    private DepartmentController departmentController;

    @Mock
    private DepartmentService departmentService;

    @Test
    @DisplayName("학과 전체 조회 성공")
    void getDepartments_Success() {
        // given
        Integer collegeIdx = 1;
        List<SearchDepartmentResponse> expectedResponses = Arrays.asList(
                new SearchDepartmentResponse(1, "컴퓨터공학과"),
                new SearchDepartmentResponse(2, "정보통신공학과")
        );
        given(departmentService.getDepartments(collegeIdx))
                .willReturn(expectedResponses);

        // when
        BaseResponse<List<SearchDepartmentResponse>> response =
                departmentController.getDepartments(collegeIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponses);
        verify(departmentService).getDepartments(collegeIdx);
    }

    @Test
    @DisplayName("학과 생성 성공")
    void createDepartment_Success() {
        // given
        User admin = createAdminUser();
        CreateDepartmentRequest request = new CreateDepartmentRequest(1,"신설학과");
        String expectedResponse = "신설학과 학과가 생성되었습니다.";

        given(departmentService.createDepartment(admin, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = departmentController.createDepartment(admin, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(departmentService).createDepartment(admin, request);
    }

    @Test
    @DisplayName("학과명 수정 성공")
    void updateDepartmentName_Success() {
        // given
        User admin = createAdminUser();
        Integer departmentIdx = 1;
        UpdateDepartmentRequest request = new UpdateDepartmentRequest("수정된학과");
        String expectedResponse = "수정된학과 학과 이름이 변경되었습니다.";

        given(departmentService.updateDepartmentName(admin, departmentIdx, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response =
                departmentController.updateDepartmentName(admin, departmentIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(departmentService).updateDepartmentName(admin, departmentIdx, request);
    }

    @Test
    @DisplayName("학과 삭제 성공")
    void deleteDepartment_Success() {
        // given
        User admin = createAdminUser();
        Integer departmentIdx = 1;
        String expectedResponse = "컴퓨터공학과 학과가 삭제되었습니다.";

        given(departmentService.deleteDepartment(admin, departmentIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response =
                departmentController.deleteDepartment(admin, departmentIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(departmentService).deleteDepartment(admin, departmentIdx);
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
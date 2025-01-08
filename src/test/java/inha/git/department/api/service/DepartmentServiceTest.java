package inha.git.department.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.department.api.mapper.DepartmentMapper;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.statistics.domain.repository.TotalDepartmentStatisticsJpaRepository;
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
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.COLLEGE_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("학과 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentJpaRepository departmentJpaRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private TotalDepartmentStatisticsJpaRepository totalDepartmentStatisticsJpaRepository;

    @Mock
    private CollegeJpaRepository collegeJpaRepository;

    @Test
    @DisplayName("학과 전체 조회 성공")
    void getDepartments_Success() {
        // given
        List<Department> departments = Arrays.asList(
                createDepartment(1, "컴퓨터공학과"),
                createDepartment(2, "정보통신공학과")
        );
        List<SearchDepartmentResponse> expectedResponses = Arrays.asList(
                new SearchDepartmentResponse(1, "컴퓨터공학과"),
                new SearchDepartmentResponse(2, "정보통신공학과")
        );

        given(departmentJpaRepository.findAllByState(ACTIVE))
                .willReturn(departments);
        given(departmentMapper.departmentsToSearchDepartmentResponses(departments))
                .willReturn(expectedResponses);

        // when
        List<SearchDepartmentResponse> result = departmentService.getDepartments(null);

        // then
        assertThat(result).isEqualTo(expectedResponses);
        verify(departmentJpaRepository).findAllByState(ACTIVE);
    }

    @Test
    @DisplayName("특정 단과대학의 학과 조회 성공")
    void getDepartments_WithCollegeId_Success() {
        // given
        Integer collegeIdx = 1;
        College college = createCollege(collegeIdx, "공과대학");
        List<Department> departments = Arrays.asList(
                createDepartment(1, "컴퓨터공학과"),
                createDepartment(2, "정보통신공학과")
        );
        List<SearchDepartmentResponse> expectedResponses = Arrays.asList(
                new SearchDepartmentResponse(1, "컴퓨터공학과"),
                new SearchDepartmentResponse(2, "정보통신공학과")
        );

        given(collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE))
                .willReturn(Optional.of(college));
        given(departmentJpaRepository.findAllByCollegeAndState(college, ACTIVE))
                .willReturn(departments);
        given(departmentMapper.departmentsToSearchDepartmentResponses(departments))
                .willReturn(expectedResponses);

        // when
        List<SearchDepartmentResponse> result = departmentService.getDepartments(collegeIdx);

        // then
        assertThat(result).isEqualTo(expectedResponses);
        verify(collegeJpaRepository).findByIdAndState(collegeIdx, ACTIVE);
        verify(departmentJpaRepository).findAllByCollegeAndState(college, ACTIVE);
    }

    @Test
    @DisplayName("단과대학이 존재하지 않을 때 예외 발생")
    void getDepartments_CollegeNotFound_ThrowsException() {
        // given
        Integer collegeIdx = 999;
        given(collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                departmentService.getDepartments(collegeIdx));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(COLLEGE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("학과 생성 성공")
    void createDepartment_Success() {
        // given
        User admin = createAdminUser();
        CreateDepartmentRequest request = new CreateDepartmentRequest(1,"신설학과");
        College college = createCollege(1, "공과대학");
        Department department = Department.builder()
                .id(1)
                .name("신설학과")
                .college(college)
                .build();

        given(collegeJpaRepository.findByIdAndState(request.collegeIdx(), ACTIVE))
                .willReturn(Optional.of(college));
        given(departmentMapper.createDepartmentRequestToDepartment(request, college))
                .willReturn(department);
        given(departmentJpaRepository.save(any(Department.class)))
                .willReturn(department);

        // when
        String result = departmentService.createDepartment(admin, request);

        // then
        assertThat(result).isEqualTo("신설학과 학과가 생성되었습니다.");
        verify(departmentJpaRepository).save(any(Department.class));
        verify(totalDepartmentStatisticsJpaRepository).save(any());
    }

    @Test
    @DisplayName("학과명 수정 성공")
    void updateDepartmentName_Success() {
        // given
        User admin = createAdminUser();
        Integer departmentIdx = 1;
        UpdateDepartmentRequest request = new UpdateDepartmentRequest("수정된학과");
        Department department = createDepartment(departmentIdx, "기존학과");

        given(departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.of(department));

        // when
        String result = departmentService.updateDepartmentName(admin, departmentIdx, request);

        // then
        assertThat(result).isEqualTo("수정된학과 학과 이름이 변경되었습니다.");
        assertThat(department.getName()).isEqualTo("수정된학과");
    }

    @Test
    @DisplayName("존재하지 않는 학과 수정 시 예외 발생")
    void updateDepartmentName_DepartmentNotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer departmentIdx = 999;
        UpdateDepartmentRequest request = new UpdateDepartmentRequest("수정된학과");

        given(departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                departmentService.updateDepartmentName(admin, departmentIdx, request))
                .isInstanceOf(BaseException.class)
                .extracting("errorReason.message")
                .isEqualTo(DEPARTMENT_NOT_FOUND.getMessage());
    }



    @Test
    @DisplayName("학과 삭제 성공")
    void deleteDepartment_Success() {
        // given
        User admin = createAdminUser();
        Integer departmentIdx = 1;
        Department department = createDepartment(departmentIdx, "삭제할학과");

        given(departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.of(department));

        // when
        String result = departmentService.deleteDepartment(admin, departmentIdx);

        // then
        assertThat(result).isEqualTo("삭제할학과 학과가 삭제되었습니다.");
        assertThat(department.getState()).isEqualTo(INACTIVE);
        assertThat(department.getDeletedAt()).isNotNull();
    }

    private College createCollege(Integer id, String name) {
        return College.builder()
                .id(id)
                .name(name)
                .build();
    }

    private Department createDepartment(Integer id, String name) {
        College college = createCollege(id, "테스트단과대학");
        return Department.builder()
                .id(id)
                .name(name)
                .college(college)
                .build();
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
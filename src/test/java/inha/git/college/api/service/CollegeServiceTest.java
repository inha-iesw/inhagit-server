package inha.git.college.api.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.college.service.CollegeServiceImpl;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
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
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("단과대 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class CollegeServiceTest {

    @InjectMocks
    private CollegeServiceImpl collegeService;

    @Mock
    private CollegeJpaRepository collegeJpaRepository;


    @Mock
    private DepartmentJpaRepository departmentJpaRepository;

    @Mock
    private CollegeMapper collegeMapper;

    @Test
    @DisplayName("단과대 전체 조회 성공")
    void getColleges_Success() {
        // given
        List<College> colleges = Arrays.asList(
                createCollege(1, "소프트웨어융합대학"),
                createCollege(2, "공과대학")
        );
        List<SearchCollegeResponse> expectedResponses = Arrays.asList(
                new SearchCollegeResponse(1, "소프트웨어융합대학"),
                new SearchCollegeResponse(2, "공과대학")
        );

        given(collegeJpaRepository.findAllByState(ACTIVE))
                .willReturn(colleges);
        given(collegeMapper.collegesToSearchCollegeResponses(colleges))
                .willReturn(expectedResponses);

        // when
        List<SearchCollegeResponse> result = collegeService.getColleges();

        // then
        assertThat(result).isEqualTo(expectedResponses);
        verify(collegeJpaRepository).findAllByState(ACTIVE);
    }

    @Test
    @DisplayName("특정 단과대 조회 성공")
    void getCollege_Success() {
        // given
        Integer departmentIdx = 1;
        Department department = createDepartment(departmentIdx, "컴퓨터공학과");
        College college = createCollege(1, "소프트웨어융합대학");
        SearchCollegeResponse expectedResponse = new SearchCollegeResponse(1, "소프트웨어융합대학");

        given(departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.of(department));
        given(collegeJpaRepository.findByDepartments_IdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.of(college));
        given(collegeMapper.collegeToSearchCollegeResponse(college))
                .willReturn(expectedResponse);

        // when
        SearchCollegeResponse result = collegeService.getCollege(departmentIdx);

        // then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("존재하지 않는 학과로 단과대 조회 시 예외 발생")
    void getCollege_DepartmentNotFound_ThrowsException() {
        // given
        Integer departmentIdx = 999;

        given(departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                collegeService.getCollege(departmentIdx));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(DEPARTMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("단과대 생성 성공")
    void createCollege_Success() {
        // given
        User admin = createAdminUser();
        CreateCollegeRequest request = new CreateCollegeRequest("신설단과대학");
        College college = createCollege(1, "신설단과대학");

        given(collegeMapper.createCollegeRequestToCollege(request))
                .willReturn(college);
        given(collegeJpaRepository.save(any(College.class)))
                .willReturn(college);

        // when
        String result = collegeService.createCollege(admin, request);

        // then
        assertThat(result).isEqualTo("신설단과대학 단과대가 생성되었습니다.");
        verify(collegeJpaRepository).save(any(College.class));
    }

    @Test
    @DisplayName("단과대 이름 수정 성공")
    void updateCollegeName_Success() {
        // given
        User admin = createAdminUser();
        Integer collegeIdx = 1;
        UpdateCollegeRequest request = new UpdateCollegeRequest("수정된단과대학");
        College college = createCollege(collegeIdx, "기존단과대학");

        given(collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE))
                .willReturn(Optional.of(college));

        // when
        String result = collegeService.updateCollegeName(admin, collegeIdx, request);

        // then
        assertThat(result).isEqualTo("수정된단과대학 단과대 이름이 변경되었습니다.");
        assertThat(college.getName()).isEqualTo("수정된단과대학");
    }

    @Test
    @DisplayName("단과대 삭제 성공")
    void deleteCollege_Success() {
        // given
        User admin = createAdminUser();
        Integer collegeIdx = 1;
        College college = createCollege(collegeIdx, "삭제할단과대학");

        given(collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE))
                .willReturn(Optional.of(college));

        // when
        String result = collegeService.deleteCollege(admin, collegeIdx);

        // then
        assertThat(result).isEqualTo("삭제할단과대학 단과대가 삭제되었습니다.");
        assertThat(college.getState()).isEqualTo(INACTIVE);
        assertThat(college.getDeletedAt()).isNotNull();
    }

    private College createCollege(Integer id, String name) {
        return College.builder()
                .id(id)
                .name(name)
                .build();
    }

    private Department createDepartment(Integer id, String name) {
        return Department.builder()
                .id(id)
                .name(name)
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
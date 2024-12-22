package inha.git.semester.service;

import inha.git.common.exceptions.BaseException;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.SEMESTER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SemesterServiceTest {

    @InjectMocks
    private SemesterServiceImpl semesterService;

    @Mock
    private SemesterJpaRepository semesterJpaRepository;

    @Mock
    private SemesterMapper semesterMapper;

    @Test
    @DisplayName("학기 전체 조회 성공")
    void getSemesters_Success() {
        // given
        List<Semester> semesters = Arrays.asList(
                createSemester(1, "2023-1"),
                createSemester(2, "2023-2")
        );
        List<SearchSemesterResponse> expectedResponses = Arrays.asList(
                new SearchSemesterResponse(1, "2023-1"),
                new SearchSemesterResponse(2, "2023-2")
        );

        given(semesterJpaRepository.findAllByState(ACTIVE, Sort.by(Sort.Direction.ASC, "name")))
                .willReturn(semesters);
        given(semesterMapper.semestersToSearchSemesterResponses(semesters))
                .willReturn(expectedResponses);

        // when
        List<SearchSemesterResponse> result = semesterService.getSemesters();

        // then
        assertThat(result).isEqualTo(expectedResponses);
        verify(semesterJpaRepository).findAllByState(ACTIVE, Sort.by(Sort.Direction.ASC, "name"));
    }

    @Test
    @DisplayName("학기 생성 성공")
    void createSemester_Success() {
        // given
        User admin = createAdminUser();
        CreateSemesterRequest request = new CreateSemesterRequest("2024-1");
        Semester semester = createSemester(1, "2024-1");

        given(semesterMapper.createSemesterRequestToSemester(request))
                .willReturn(semester);
        given(semesterJpaRepository.save(any(Semester.class)))
                .willReturn(semester);

        // when
        String result = semesterService.createSemester(admin, request);

        // then
        assertThat(result).isEqualTo("2024-1 학기가 생성되었습니다.");
        verify(semesterJpaRepository).save(any(Semester.class));
    }

    @Test
    @DisplayName("학기명 수정 성공")
    void updateSemesterName_Success() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 1;
        UpdateSemesterRequest request = new UpdateSemesterRequest("2024-2");
        Semester semester = createSemester(semesterIdx, "2024-1");

        given(semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE))
                .willReturn(Optional.of(semester));

        // when
        String result = semesterService.updateSemesterName(admin, semesterIdx, request);

        // then
        assertThat(result).isEqualTo("2024-2 학기 이름이 수정되었습니다.");
        assertThat(semester.getName()).isEqualTo("2024-2");
    }

    @Test
    @DisplayName("존재하지 않는 학기 수정 시 예외 발생")
    void updateSemesterName_NotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 999;
        UpdateSemesterRequest request = new UpdateSemesterRequest("2024-2");

        given(semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                semesterService.updateSemesterName(admin, semesterIdx, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(SEMESTER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("학기 삭제 성공")
    void deleteSemester_Success() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 1;
        Semester semester = createSemester(semesterIdx, "2024-1");

        given(semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE))
                .willReturn(Optional.of(semester));

        // when
        String result = semesterService.deleteSemester(admin, semesterIdx);

        // then
        assertThat(result).isEqualTo("2024-1 학기가 삭제되었습니다.");
        assertThat(semester.getState()).isEqualTo(INACTIVE);
        assertThat(semester.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 학기 삭제 시 예외 발생")
    void deleteSemester_NotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer semesterIdx = 999;

        given(semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                semesterService.deleteSemester(admin, semesterIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(SEMESTER_NOT_FOUND.getMessage());
    }

    private Semester createSemester(Integer id, String name) {
        return Semester.builder()
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
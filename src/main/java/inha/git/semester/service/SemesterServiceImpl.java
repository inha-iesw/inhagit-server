package inha.git.semester.service;

import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.SEMESTER_NOT_FOUND;

/**
 * SemesterServiceImpl는 SemesterService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SemesterServiceImpl implements SemesterService {

    private final SemesterJpaRepository semesterJpaRepository;
    private final SemesterMapper semesterMapper;


    /**
     * 학기 전체 조회
     *
     * @return 학기 전체 조회 결과
     */
    @Override
    public List<SearchSemesterResponse> getSemesters() {
        return semesterMapper.semestersToSearchSemesterResponses
                (semesterJpaRepository.findAllByState(BaseEntity.State.ACTIVE, Sort.by(Sort.Direction.ASC, "name")));
    }

    /**
     * 학기 생성
     *
     * @param createDepartmentRequest 학기 생성 요청
     * @return 생성된 학기 이름
     */
    @Override
    @Transactional
    public String createSemester(User admin, CreateSemesterRequest createDepartmentRequest) {
        Semester semester = semesterJpaRepository.save(semesterMapper.createSemesterRequestToSemester(createDepartmentRequest));
        log.info("학기 생성 성공 - 관리자: {} 학기명: {}", admin.getName(), createDepartmentRequest.name());
        return semester.getName() + " 학기가 생성되었습니다.";
    }

    /**
     * 학기 이름 수정
     *
     * @param semesterIdx 학기 인덱스
     * @param updateSemesterRequest 학기 수정 요청
     * @return 수정된 학기 이름
     */
    @Override
    @Transactional
    public String updateSemesterName(User admin, Integer semesterIdx, UpdateSemesterRequest updateSemesterRequest) {
        log.info("semesterIdx {}", semesterIdx);
        Semester semester = semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        semester.setName(updateSemesterRequest.name());
        log.info("학기 이름 수정 성공 - 관리자: {} 학기명: {}", admin.getName(), updateSemesterRequest.name());
        return semester.getName() + " 학기 이름이 수정되었습니다.";
    }

    /**
     * 학기 삭제
     *
     * @param semesterIdx 학기 인덱스
     * @return 삭제된 학기 이름
     */
    @Override
    @Transactional
    public String deleteSemester(User admin, Integer semesterIdx) {
        Semester semester = semesterJpaRepository.findByIdAndState(semesterIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        semester.setState(INACTIVE);
        semester.setDeletedAt();
        log.info("학기 삭제 성공 - 관리자: {} 학기명: {}", admin.getName(), semester.getName());
        return semester.getName() + " 학기가 삭제되었습니다.";
    }
}

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
 * 학기 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 학기의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SemesterServiceImpl implements SemesterService {

    private final SemesterJpaRepository semesterJpaRepository;
    private final SemesterMapper semesterMapper;


    /**
     * 활성화된 모든 학기를 조회합니다.
     *
     * @return 학기 정보 목록 (SearchSemesterResponse)
     */
    @Override
    public List<SearchSemesterResponse> getSemesters() {
        return semesterMapper.semestersToSearchSemesterResponses
                (semesterJpaRepository.findAllByState(BaseEntity.State.ACTIVE, Sort.by(Sort.Direction.ASC, "name")));
    }

    /**
     * 새로운 학기를 생성합니다.
     *
     * @param admin 생성을 요청한 관리자 정보
     * @param createSemesterRequest 생성할 학기 정보
     * @return 학기 생성 완료 메시지
     */
    @Override
    @Transactional
    public String createSemester(User admin, CreateSemesterRequest createSemesterRequest) {
        Semester semester = semesterJpaRepository.save(semesterMapper.createSemesterRequestToSemester(createSemesterRequest));
        log.info("학기 생성 성공 - 관리자: {} 학기명: {}", admin.getName(), createSemesterRequest.name());
        return semester.getName() + " 학기가 생성되었습니다.";
    }

    /**
     * 학기명을 수정합니다.
     *
     * @param admin 수정을 요청한 관리자 정보
     * @param semesterIdx 수정할 학기의 식별자
     * @param updateSemesterRequest 새로운 학기명 정보
     * @return 학기명 수정 완료 메시지
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
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
     * 학기를 삭제(비활성화) 처리합니다.
     *
     * @param admin 삭제를 요청한 관리자 정보
     * @param semesterIdx 삭제할 학기의 식별자
     * @return 학기 삭제 완료 메시지
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
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

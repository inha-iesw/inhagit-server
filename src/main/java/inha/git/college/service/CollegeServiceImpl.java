package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.COLLEGE_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;

/**
 * 단과대학 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 단과대학의 조회, 생성, 수정, 삭제 및 관련 통계 처리를 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CollegeServiceImpl implements CollegeService {

    private final CollegeJpaRepository collegeJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;
    private final CollegeMapper collegeMapper;

    /**
     * 모든 활성화된 단과대학을 조회합니다.
     *
     * @return 단과대학 목록
     */
    @Override
    public List<SearchCollegeResponse> getColleges() {
        return collegeMapper.collegesToSearchCollegeResponses(collegeJpaRepository.findAllByState(ACTIVE));
    }


    /**
     * 특정 학과가 속한 단과대학을 조회합니다.
     *
     * @param departmentIdx 조회할 학과의 식별자
     * @return 해당 학과의 단과대학 정보
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우,
     *                      COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @Override
    public SearchCollegeResponse getCollege(Integer departmentIdx) {
        departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        College college = collegeJpaRepository.findByDepartments_IdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        return collegeMapper.collegeToSearchCollegeResponse(college);
    }

    /**
     * 새로운 단과대학을 생성합니다.
     *
     * @param admin 생성을 요청한 관리자 정보
     * @param createDepartmentRequest 생성할 단과대학 정보
     * @return 단과대학 생성 완료 메시지
     */
    @Override
    @Transactional
    public String createCollege(User admin, CreateCollegeRequest createDepartmentRequest) {
        College college = collegeJpaRepository.save
                (collegeMapper.createCollegeRequestToCollege(createDepartmentRequest));
        log.info("단과대 생성 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대가 생성되었습니다.";
    }


    /**
     * 단과대학의 이름을 수정합니다.
     *
     * @param admin 수정을 요청한 관리자 정보
     * @param collegeIdx 수정할 단과대학의 식별자
     * @param updateCollegeRequest 새로운 단과대학 정보
     * @return 단과대학 수정 완료 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String updateCollegeName(User admin, Integer collegeIdx ,UpdateCollegeRequest updateCollegeRequest) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setName(updateCollegeRequest.name());
        log.info("단과대 이름 수정 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대 이름이 변경되었습니다.";
    }

    /**
     * 단과대학을 삭제(비활성화) 처리합니다.
     *
     * @param admin 삭제를 요청한 관리자 정보
     * @param collegeIdx 삭제할 단과대학의 식별자
     * @return 단과대학 삭제 완료 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String deleteCollege(User admin, Integer collegeIdx) {
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        college.setState(INACTIVE);
        college.setDeletedAt();
        log.info("단과대 삭제 성공 - 관리자: {} 단과대 이름: {}", admin.getName(), college.getName());
        return college.getName() + " 단과대가 삭제되었습니다.";
    }
}

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * 학과 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 학과의 조회, 생성, 수정, 삭제 및 관련 통계 처리를 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService{
    private final DepartmentJpaRepository departmentJpaRepository;
    private final DepartmentMapper departmentMapper;
    private final TotalDepartmentStatisticsJpaRepository totalDepartmentStatisticsJpaRepository;
    private final CollegeJpaRepository collegeJpaRepository;

    /**
     * 학과 목록을 조회합니다.
     *
     * @param collegeIdx 조회할 단과대학 ID (선택적)
     * @return 학과 목록
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @Override
    public List<SearchDepartmentResponse> getDepartments(Integer collegeIdx) {
        if(collegeIdx == null) {
            return departmentMapper.departmentsToSearchDepartmentResponses(departmentJpaRepository.findAllByState(ACTIVE));
        }
        College college = collegeJpaRepository.findByIdAndState(collegeIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        return departmentMapper.departmentsToSearchDepartmentResponses(departmentJpaRepository.findAllByCollegeAndState(college, ACTIVE));
    }

    /**
     * 새로운 학과를 생성합니다.
     *
     * @param admin 생성을 요청한 관리자 정보
     * @param createDepartmentRequest 생성할 학과 정보
     * @return 학과 생성 완료 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우,
     *                      DEPARTMENT_NOT_BELONG_TO_COLLEGE: 학과와 단과대학 정보가 일치하지 않는 경우
     */
    @Override
    @Transactional
    public String createDepartment(User admin, CreateDepartmentRequest createDepartmentRequest) {
        College college = collegeJpaRepository.findByIdAndState(createDepartmentRequest.collegeIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(COLLEGE_NOT_FOUND));
        Department department = departmentMapper.createDepartmentRequestToDepartment(createDepartmentRequest, college);
        if(!department.getCollege().getId().equals(college.getId())) {
            log.error("학과 생성 실패 {} {} - 대학과 학과가 일치하지 않습니다.", admin.getName(), createDepartmentRequest.name());
                throw new BaseException(DEPARTMENT_NOT_BELONG_TO_COLLEGE);
        }
        Department savedDepartment = departmentJpaRepository.save(department);
        totalDepartmentStatisticsJpaRepository.save(departmentMapper.createTotalDepartmentStatistics(department));
        log.info("학과 생성 성공 - 관리자: {} 학과명: {}", admin.getName(), savedDepartment.getName());
        return savedDepartment.getName() + " 학과가 생성되었습니다.";
    }

    /**
     * 학과명을 수정합니다.
     *
     * @param admin 수정을 요청한 관리자 정보
     * @param departmentIdx 수정할 학과의 식별자
     * @param updateDepartmentRequest 새로운 학과명
     * @return 학과명 수정 완료 메시지
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String updateDepartmentName(User admin, Integer departmentIdx, UpdateDepartmentRequest updateDepartmentRequest) {
        Department department = departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        department.setName(updateDepartmentRequest.name());
        log.info("학과 이름 수정 성공 - 관리자: {} 학과명: {}", admin.getName(), department.getName());
        return department.getName() + " 학과 이름이 변경되었습니다.";
    }

    /**
     * 학과를 삭제(비활성화) 처리합니다.
     *
     * @param admin 삭제를 요청한 관리자 정보
     * @param departmentIdx 삭제할 학과의 식별자
     * @return 학과 삭제 완료 메시지
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String deleteDepartment(User admin, Integer departmentIdx) {
        Department department = departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        department.setState(INACTIVE);
        department.setDeletedAt();
        log.info("학과 삭제 성공 - 관리자: {} 학과명: {}", admin.getName(), department.getName());
        return department.getName() + " 학과가 삭제되었습니다.";
    }


}

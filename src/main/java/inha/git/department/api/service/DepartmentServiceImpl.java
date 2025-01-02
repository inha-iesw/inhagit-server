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
 * DepartmentServiceImpl는 DepartmentService 인터페이스를 구현하는 클래스.
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
     * 학과 전체 조회
     *
     * @param collegeIdx 대학 인덱스
     * @return 학과 전체 조회 결과
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
     * 학과 생성
     *
     * @param createDepartmentRequest 학과 생성 요청
     * @return 생성된 학과 이름
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
     * 학과 이름 변경
     *
     * @param departmentIdx 학과 인덱스
     * @param updateDepartmentRequest 학과 이름 변경 요청
     * @return 변경된 학과 이름
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

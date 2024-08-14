package inha.git.department.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.department.api.mapper.DepartmentMapper;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.DEPARTMENT_NOT_FOUND;

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

    /**
     * 학과 전체 조회
     *
     * @return 학과 전체 조회 결과
     */
    @Override
    public List<SearchDepartmentResponse> getDepartments() {
        return departmentMapper.departmentsToSearchDepartmentResponses(departmentJpaRepository.findAllByState(ACTIVE));
    }

    /**
     * 학과 생성
     *
     * @param createDepartmentRequest 학과 생성 요청
     * @return 생성된 학과 이름
     */
    @Override
    @Transactional
    public String createDepartment(CreateDepartmentRequest createDepartmentRequest) {
        Department department = departmentMapper.createDepartmentRequestToDepartment(createDepartmentRequest);
        return departmentJpaRepository.save(department).getName() + " 학과가 생성되었습니다.";
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
    public String updateDepartmentName(Integer departmentIdx, UpdateDepartmentRequest updateDepartmentRequest) {
        Department department = departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        department.setName(updateDepartmentRequest.name());
        return department.getName() + " 학과 이름이 변경되었습니다.";
    }

    @Override
    @Transactional
    public String deleteDepartment(Integer departmentIdx) {
        Department department = departmentJpaRepository.findByIdAndState(departmentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(DEPARTMENT_NOT_FOUND));
        department.setState(INACTIVE);
        department.setDeletedAt();
        return department.getName() + " 학과가 삭제되었습니다.";
    }


}

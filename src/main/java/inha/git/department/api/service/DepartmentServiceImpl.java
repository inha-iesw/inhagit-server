package inha.git.department.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.mapper.DepartmentMapper;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return departmentMapper.departmentsToSearchDepartmentResponses(departmentJpaRepository.findAll());
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


}

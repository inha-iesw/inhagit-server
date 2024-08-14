package inha.git.department.api.mapper;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.domain.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * DepartmentMapper는 Department 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    /**
     * CreateDepartmentRequest를 Department 엔티티로 변환
     *
     * @param createDepartmentRequest 기업 생성 요청
     * @return Department 엔티티
     */
    Department createDepartmentRequestToDepartment(CreateDepartmentRequest createDepartmentRequest);

    @Mapping(source = "department.id", target = "idx")
    SearchDepartmentResponse departmentToSearchDepartmentResponse(Department department);
    List<SearchDepartmentResponse> departmentsToSearchDepartmentResponses(List<Department> departmentList);
}

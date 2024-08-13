package inha.git.department.api.converter;

import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.domain.Department;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * DepartmentMapper는 Department 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {

    /**
     * CreateDepartmentRequest를 Department 엔티티로 변환
     *
     * @param request 부서 생성 요청
     * @return Department 엔티티
     */
    Department createDepartmentRequestToDepartment(CreateDepartmentRequest request);
}

package inha.git.department.api.mapper;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.college.domain.College;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.domain.Department;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.DepartmentStatistics;
import inha.git.statistics.domain.id.DepartmentStatisticsId;
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
     * @param college 단과대 엔티티
     * @return Department 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "college", source = "college")
    @Mapping(target = "name", source = "createDepartmentRequest.name")
    Department createDepartmentRequestToDepartment(CreateDepartmentRequest createDepartmentRequest, College college);

    @Mapping(source = "department.id", target = "idx")
    SearchDepartmentResponse departmentToSearchDepartmentResponse(Department department);
    List<SearchDepartmentResponse> departmentsToSearchDepartmentResponses(List<Department> departmentList);


    /**
     * Department 엔티티를 DepartmentStatistics 엔티티로 변환
     *
     * @param department Department 엔티티
     * @param semester Semester 엔티티
     * @param field Field 엔티티
     * @return DepartmentStatistics 엔티티
     */
    default DepartmentStatistics createDepartmentStatistics(Department department, Semester semester, Field field) {
        return new DepartmentStatistics(new DepartmentStatisticsId(department.getId(), semester.getId(), field.getId()), department, semester, field, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}

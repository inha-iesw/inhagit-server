package inha.git.semester.mapper;

import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.domain.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * SemesterMapper는 Semester 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SemesterMapper {

    @Mapping(target = "id", ignore = true)
    Semester createSemesterRequestToSemester(CreateSemesterRequest createDepartmentRequest);

}

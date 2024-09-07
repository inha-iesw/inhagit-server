package inha.git.college.mapper;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.domain.College;
import inha.git.statistics.domain.CollegeStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * CollegeMapper는 College 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollegeMapper {


    @Mapping(target = "id", ignore = true)
    College createCollegeRequestToCollege(CreateCollegeRequest createDepartmentRequest);


    @Mapping(source = "id", target = "collegeId")
    @Mapping(target = "projectCount", constant = "0")
    @Mapping(target = "questionCount", constant = "0")
    @Mapping(target = "teamCount", constant = "0")
    @Mapping(target = "problemCount", constant = "0")
    @Mapping(target = "patentCount", constant = "0")
    @Mapping(target = "projectUserCount", constant = "0")
    @Mapping(target = "questionUserCount", constant = "0")
    @Mapping(target = "teamUserCount", constant = "0")
    @Mapping(target = "patentUserCount", constant = "0")
    @Mapping(target = "problemUserCount", constant = "0")
    @Mapping(target = "problemParticipationCount", constant = "0")
    CollegeStatistics toCollegeStatistics(Integer id);
}

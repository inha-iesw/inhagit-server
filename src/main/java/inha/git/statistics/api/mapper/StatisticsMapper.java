package inha.git.statistics.api.mapper;

import inha.git.department.domain.Department;
import inha.git.statistics.api.controller.dto.response.*;
import inha.git.statistics.domain.DepartmentStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * StatisticsMapper는 Statistics 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticsMapper {

    ProjectStatisticsResponse toProjectStatisticsResponse(Integer projectCount, Integer userCount);

    QuestionStatisticsResponse toQuestionStatisticsResponse(Integer questionCount, Integer userCount);
    TeamStatisticsResponse toTeamStatisticsResponse(Integer teamCount, Integer userCount);
    ProblemStatisticsResponse toProblemStatisticsResponse(Integer problemCount, Integer userCount);

    @Mapping(target = "idx", source = "department.id")
    @Mapping(target = "name", source = "department.name")
    @Mapping(target = "projectCount", source = "departmentStatistics.projectCount")
    @Mapping(target = "questionCount", source = "departmentStatistics.questionCount")
    @Mapping(target = "problemCount", source = "departmentStatistics.problemUserCount")
    HomeStatisticsResponse toHomeStatisticsResponse(Department department, DepartmentStatistics departmentStatistics);
}

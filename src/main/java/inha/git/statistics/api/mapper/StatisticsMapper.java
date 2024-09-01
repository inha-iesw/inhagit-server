package inha.git.statistics.api.mapper;

import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * StatisticsMapper는 Statistics 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticsMapper {

    ProjectStatisticsResponse toProjectStatisticsResponse(Integer projectCount, Integer userCount);
}

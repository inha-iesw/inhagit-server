package inha.git.bug_report.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;


/**
 * BugReportMapper는 버그 제보를 변환하는 인터페이스.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BugReportMapper {


}

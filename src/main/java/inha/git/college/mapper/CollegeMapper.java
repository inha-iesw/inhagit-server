package inha.git.college.mapper;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.id.CollegeStatisticsStatisticsId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * CollegeMapper는 College 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CollegeMapper {


    /**
     * CreateCollegeRequest를 College 엔티티로 변환
     *
     * @param createDepartmentRequest CreateCollegeRequest
     * @return College 엔티티
     */
    @Mapping(target = "id", ignore = true)
    College createCollegeRequestToCollege(CreateCollegeRequest createDepartmentRequest);


    /**
     * College 엔티티를 CollegeStatistics 엔티티로 변환
     *
     * @param id College 엔티티의 id
     * @return CollegeStatistics 엔티티
     */
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

    default CollegeStatistics createCollegeStatistics(College college, Semester semester, Field field) {
        return new CollegeStatistics(
                new CollegeStatisticsStatisticsId(college.getId(), semester.getId(), field.getId()), college, semester, field, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }


    @Mapping(source = "college.id", target = "idx")
    SearchCollegeResponse collegeToSearchCollegeResponse(College college);

    List<SearchCollegeResponse> collegesToSearchCollegeResponses(List<College> collegeList);
}

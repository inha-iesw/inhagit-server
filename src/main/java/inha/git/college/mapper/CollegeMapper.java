package inha.git.college.mapper;

import inha.git.category.domain.Category;
import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.domain.College;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.TotalCollegeStatistics;
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
     * College 엔티티를 SearchCollegeResponse로 변환
     *
     * @param college College
     * @return SearchCollegeResponse
     */
    default CollegeStatistics createCollegeStatistics(College college, Semester semester, Field field, Category category) {
        return new CollegeStatistics(
                new CollegeStatisticsStatisticsId(college.getId(), semester.getId(), field.getId(), category.getId()), college, semester, field, category, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }


    @Mapping(source = "college.id", target = "idx")
    SearchCollegeResponse collegeToSearchCollegeResponse(College college);

    List<SearchCollegeResponse> collegesToSearchCollegeResponses(List<College> collegeList);

    @Mapping(source = "college.id", target = "collegeId")
    @Mapping(target = "userProjectCount", constant = "0")
    @Mapping(target = "userQuestionCount", constant = "0")
    @Mapping(target = "userProblemCount", constant = "0")
    @Mapping(target = "userTeamCount", constant = "0")
    @Mapping(target = "userPatentCount", constant = "0")
    @Mapping(target = "totalProjectCount", constant = "0")
    @Mapping(target = "totalGithubProjectCount", constant = "0")
    @Mapping(target = "totalQuestionCount", constant = "0")
    @Mapping(target = "totalProblemCount", constant = "0")
    @Mapping(target = "totalTeamCount", constant = "0")
    @Mapping(target = "totalPatentCount", constant = "0")
    TotalCollegeStatistics createTotalCollegeStatistics(College college);
}

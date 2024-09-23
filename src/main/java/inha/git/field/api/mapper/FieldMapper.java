package inha.git.field.api.mapper;

import inha.git.college.domain.College;
import inha.git.department.domain.Department;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.domain.Field;
import inha.git.semester.domain.Semester;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.DepartmentStatistics;
import inha.git.statistics.domain.UserCountStatistics;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.id.CollegeStatisticsStatisticsId;
import inha.git.statistics.domain.id.DepartmentStatisticsId;
import inha.git.statistics.domain.id.UserCountStatisticsId;
import inha.git.statistics.domain.id.UserStatisticsId;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * FieldMapper는 Field 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FieldMapper {

    /**
     * CreateFieldRequest를 Field 엔티티로 변환
     *
     * @param createFieldRequest 분야 생성 요청
     * @return Field 엔티티
     */
    Field createFieldRequestToField(CreateFieldRequest createFieldRequest);

    /**
     * Field 엔티티를 SearchFieldResponse로 변환
     *
     * @param field 분야 정보
     * @return SearchFieldResponse
     */
    @Mapping(source = "id", target = "idx")
    SearchFieldResponse fieldToSearchFieldResponse(Field field);
    /**
     * Field 엔티티 목록을 SearchFieldResponse 목록으로 변환
     *
     * @param fields 분야 정보 목록
     * @return SearchFieldResponse 목록
     */
    List<SearchFieldResponse> fieldsToSearchFieldResponses(List<Field> fields);


    default List<CollegeStatistics> createCollegeStatistics(Field field, List<College> colleges, List<Semester> semesters) {
        List<CollegeStatistics> statisticsList = new ArrayList<>();
        for (College college : colleges) {
            for (Semester semester : semesters) {
                CollegeStatistics statistics = CollegeStatistics.builder()
                        .id(new CollegeStatisticsStatisticsId(college.getId(), semester.getId(), field.getId())) // 복합 키 설정
                        .college(college)
                        .semester(semester)
                        .field(field)
                        .projectCount(0) // 기본 값 설정
                        .githubProjectCount(0)
                        .questionCount(0)
                        .problemCount(0)
                        .teamCount(0)
                        .patentCount(0)
                        .projectUserCount(0)
                        .questionUserCount(0)
                        .problemUserCount(0)
                        .teamUserCount(0)
                        .patentUserCount(0)
                        .problemParticipationCount(0)
                        .build();

                statisticsList.add(statistics);
            }
        }
        return statisticsList;
    }

    default List<DepartmentStatistics> createDepartmentStatistics(Field savedField, List<Department> departments, List<Semester> semesters) {
        List<DepartmentStatistics> statisticsList = new ArrayList<>();
        for (Department department : departments) {
            for (Semester semester : semesters) {
                DepartmentStatistics statistics = DepartmentStatistics.builder()
                        .id(new DepartmentStatisticsId(department.getId(), semester.getId(), savedField.getId())) // 복합 키 설정
                        .department(department)
                        .semester(semester)
                        .field(savedField)
                        .projectCount(0) // 기본 값 설정
                        .githubProjectCount(0)
                        .questionCount(0)
                        .problemCount(0)
                        .teamCount(0)
                        .patentCount(0)
                        .projectUserCount(0)
                        .questionUserCount(0)
                        .problemUserCount(0)
                        .teamUserCount(0)
                        .patentUserCount(0)
                        .problemParticipationCount(0)
                        .build();

                statisticsList.add(statistics);
            }
        }
        return statisticsList;
    }

    default List<UserStatistics> createUserStatistics(Field savedField, List<User> users, List<Semester> semesters) {
        List<UserStatistics> statisticsList = new ArrayList<>();
        for (User user : users) {
            for (Semester semester : semesters) {
                UserStatistics statistics = UserStatistics.builder()
                        .id(new UserStatisticsId(user.getId(), semester.getId(), savedField.getId())) // 복합 키 설정
                        .user(user)
                        .semester(semester)
                        .field(savedField)
                        .projectCount(0) // 기본 값 설정
                        .githubProjectCount(0)
                        .questionCount(0)
                        .problemCount(0)
                        .teamCount(0)
                        .patentCount(0)
                        .build();

                statisticsList.add(statistics);
            }
        }
        return statisticsList;
    }

    default List<UserCountStatistics> createUserCountStatistics(Field savedField, List<Semester> semesters) {
        List<UserCountStatistics> statisticsList = new ArrayList<>();
            for (Semester semester : semesters) {
                UserCountStatistics statistics = UserCountStatistics.builder()
                        .id(new UserCountStatisticsId(semester.getId(), savedField.getId())) // 복합 키 설정
                        .semester(semester)
                        .field(savedField)
                        .userProjectCount(0) // 기본 값 설정
                        .userQuestionCount(0)
                        .userProblemCount(0)
                        .userTeamCount(0)
                        .userPatentCount(0)
                        .totalProjectCount(0)
                        .totalGithubProjectCount(0)
                        .totalQuestionCount(0)
                        .totalProblemCount(0)
                        .totalTeamCount(0)
                        .totalPatentCount(0)
                        .build();

                statisticsList.add(statistics);
            }
        return statisticsList;
    }
}

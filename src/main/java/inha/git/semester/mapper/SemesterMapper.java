package inha.git.semester.mapper;

import inha.git.category.domain.Category;
import inha.git.college.domain.College;
import inha.git.department.domain.Department;
import inha.git.field.domain.Field;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
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
 * SemesterMapper는 Semester 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SemesterMapper {

    @Mapping(target = "id", ignore = true)
    Semester createSemesterRequestToSemester(CreateSemesterRequest createDepartmentRequest);


    @Mapping(source = "semester.id", target = "idx")
    SearchSemesterResponse semesterToSearchSemesterResponse(Semester semester);

    List<SearchSemesterResponse> semestersToSearchSemesterResponses(List<Semester> semesterList);

    default List<CollegeStatistics> createCollegeStatistics(Semester semester, List<College> colleges, List<Field> fields, List<Category> categories) {
        List<CollegeStatistics> statisticsList = new ArrayList<>();
        for (College college : colleges) {
            for (Field field : fields) {
                for (Category category : categories) {
                    CollegeStatistics statistics = CollegeStatistics.builder()
                            .id(new CollegeStatisticsStatisticsId(college.getId(), semester.getId(), field.getId(), category.getId()))
                            .college(college)
                            .semester(semester)
                            .field(field)
                            .category(category)
                            .projectCount(0)
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
        }
        return statisticsList;
    }

    default List<DepartmentStatistics> createDepartmentStatistics(Semester semester, List<Department> departments, List<Field> fields, List<Category> categories) {
        List<DepartmentStatistics> statisticsList = new ArrayList<>();
        for (Department department : departments) {
            for (Field field : fields) {
                for (Category category : categories) {
                    DepartmentStatistics statistics = DepartmentStatistics.builder()
                            .id(new DepartmentStatisticsId(department.getId(), semester.getId(), field.getId(), category.getId()))
                            .department(department)
                            .semester(semester)
                            .field(field)
                            .category(category)
                            .projectCount(0)
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
        }
        return statisticsList;
    }


    default List<UserStatistics> createUserStatistics(Semester semester, List<User> users, List<Field> fields, List<Category> categories) {
        List<UserStatistics> statisticsList = new ArrayList<>();
        for (User user : users) {
            for (Field field : fields) {
                for (Category category : categories) {
                    UserStatistics statistics = UserStatistics.builder()
                            .id(new UserStatisticsId(user.getId(), semester.getId(), field.getId(), category.getId()))
                            .user(user)
                            .semester(semester)
                            .field(field)
                            .category(category)
                            .projectCount(0)
                            .githubProjectCount(0)
                            .questionCount(0)
                            .problemCount(0)
                            .teamCount(0)
                            .patentCount(0)
                            .build();
                    statisticsList.add(statistics);
                }
            }
        }
        return statisticsList;
    }

    default List<UserCountStatistics> createUserCountStatistics(Semester semester, List<Field> fields, List<Category> categories) {
        List<UserCountStatistics> statisticsList = new ArrayList<>();
        for (Field field : fields) {
            for (Category category : categories) {
                UserCountStatistics statistics = UserCountStatistics.builder()
                        .id(new UserCountStatisticsId(semester.getId(), field.getId(), category.getId()))
                        .semester(semester)
                        .field(field)
                        .userProjectCount(0)
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
        }
        return statisticsList;
    }
}

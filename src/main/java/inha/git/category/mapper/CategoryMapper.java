package inha.git.category.mapper;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.domain.Category;
import inha.git.college.domain.College;
import inha.git.department.domain.Department;
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
 * CategoryMapper는 Category 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category createCategoryRequestToSemester(CreateCategoryRequest createCategoryRequest);


    @Mapping(source = "category.id", target = "idx")
    SearchCategoryResponse categoryToCategoryResponse(Category category);

    List<SearchCategoryResponse> categoriesToSearchCategoryResponses(List<Category> categoryList);

    default List<CollegeStatistics> createCollegeStatistics(Category category, List<College> colleges, List<Field> fields, List<Semester> semesters) {
        List<CollegeStatistics> statisticsList = new ArrayList<>();
        for (College college : colleges) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
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

    default List<DepartmentStatistics> createDepartmentStatistics(Category category, List<Department> departments, List<Field> fields, List<Semester> semesters) {
        List<DepartmentStatistics> statisticsList = new ArrayList<>();
        for (Department department : departments) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
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

    default List<UserStatistics> createUserStatistics(Category category, List<User> users, List<Field> fields, List<Semester> semesters) {
        List<UserStatistics> statisticsList = new ArrayList<>();
        for (User user : users) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
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

    default List<UserCountStatistics> createUserCountStatistics(Category category, List<Field> fields, List<Semester> semesters) {
        List<UserCountStatistics> statisticsList = new ArrayList<>();
        for (Semester semester : semesters) {
            for (Field field : fields) {
                UserCountStatistics statistics = UserCountStatistics.builder()
                        .id(new UserCountStatisticsId(semester.getId(), field.getId(), category.getId())) // 복합 키 설정
                        .semester(semester)
                        .field(field)
                        .category(category)
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
        }
        return statisticsList;
    }


}

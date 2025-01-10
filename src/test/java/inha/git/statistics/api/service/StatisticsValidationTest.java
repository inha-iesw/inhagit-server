package inha.git.statistics.api.service;

import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.domain.DepartmentStatistics;
import inha.git.statistics.domain.TotalUserStatistics;
import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticsValidationTest {
    @Autowired
    private ProjectJpaRepository projectRepository;
    @Autowired
    private UserStatisticsJpaRepository userStatisticsRepository;
    @Autowired
    private CollegeStatisticsJpaRepository collegeStatisticsRepository;
    @Autowired
    private DepartmentStatisticsJpaRepository departmentStatisticsRepository;
    @Autowired
    private TotalUserStatisticsJpaRepository totalUserStatisticsRepository;
    @Autowired
    private ProjectStatisticsQueryRepository projectStatisticsQueryRepository;

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

    @Autowired
    private SemesterJpaRepository semesterJpaRepository;

    @Test
    @DisplayName("사용자별 프로젝트 통계 정합성 검증")
    void validateUserProjectStatistics() {
        // 1. 실제 프로젝트 수
        Map<Integer, Long> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .collect(Collectors.groupingBy(
                        p -> p.getUser().getId(),
                        Collectors.counting()
                ));

        // 2. UserStatistics의 프로젝트 수
        List<UserStatistics> userStats = userStatisticsRepository.findAll();
        Map<Integer, Integer> statsProjectCounts = userStats.stream()
                .collect(Collectors.groupingBy(
                        us -> us.getUser().getId(),
                        Collectors.summingInt(UserStatistics::getProjectCount)
                ));

        // 3. 검증 및 결과 출력
        actualCounts.forEach((userId, actualCount) -> {
            Integer statsCount = statsProjectCounts.getOrDefault(userId, 0);

            System.out.println("User ID: " + userId);
            System.out.println("Actual projects: " + actualCount);
            System.out.println("Statistics count: " + statsCount);
            System.out.println("Difference: " + (statsCount - actualCount));
            System.out.println("------------------------");
        });
    }

    @Test
    @DisplayName("학과별 프로젝트 통계 정합성 검증")
    void validateDepartmentProjectStatistics() {
        // 1. 실제 학과별 프로젝트 수
        Map<Integer, Long> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .flatMap(p -> p.getUser().getUserDepartments().stream())
                .collect(Collectors.groupingBy(
                        ud -> ud.getDepartment().getId(),
                        Collectors.counting()
                ));

        // 2. DepartmentStatistics의 프로젝트 수
        Map<Integer, Integer> statsCounts = departmentStatisticsRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        ds -> ds.getDepartment().getId(),
                        Collectors.summingInt(DepartmentStatistics::getProjectCount)
                ));

        // 3. 결과 출력
        actualCounts.forEach((deptId, actualCount) -> {
            Integer statsCount = statsCounts.getOrDefault(deptId, 0);

            System.out.println("Department ID: " + deptId);
            System.out.println("Actual projects: " + actualCount);
            System.out.println("Statistics count: " + statsCount);
            System.out.println("Difference: " + (statsCount - actualCount));
            System.out.println("------------------------");
        });
    }

    @Test
    @DisplayName("전체 통계 정합성 검증")
    void validateTotalStatistics2() {
        // 1. 실제 프로젝트 수
        long actualProjectCount = projectRepository.count();
        long actualGithubProjectCount = projectRepository.findAll().stream()
                .filter(p -> p.getRepoName() != null)
                .count();

        // 2. 전체 통계값
        TotalUserStatistics totalStats = totalUserStatisticsRepository
                .findById(1)
                .orElseThrow();

        // 3. 결과 출력
        System.out.println("=== Total Projects ===");
        System.out.println("Actual count: " + actualProjectCount);
        System.out.println("Statistics count: " + totalStats.getTotalProjectCount());
        System.out.println("Difference: " +
                (totalStats.getTotalProjectCount() - actualProjectCount));

        System.out.println("\n=== Github Projects ===");
        System.out.println("Actual count: " + actualGithubProjectCount);
        System.out.println("Statistics count: " + totalStats.getTotalGithubProjectCount());
        System.out.println("Difference: " +
                (totalStats.getTotalGithubProjectCount() - actualGithubProjectCount));
    }

    @Test
    @DisplayName("학기별 프로젝트 통계 정합성 검증")
    void validateSemesterProjectStatistics() {
        // 1. 실제 학기별 프로젝트 수
        Map<Integer, Long> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getSemester() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getSemester().getId(),
                        Collectors.counting()
                ));

        // 2. 학기별 통계 수
        Map<Integer, Integer> statsCounts = userStatisticsRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        us -> us.getSemester().getId(),
                        Collectors.summingInt(UserStatistics::getProjectCount)
                ));

        // 3. 결과 출력
        actualCounts.forEach((semesterId, actualCount) -> {
            Integer statsCount = statsCounts.getOrDefault(semesterId, 0);

            System.out.println("Semester ID: " + semesterId);
            System.out.println("Actual projects: " + actualCount);
            System.out.println("Statistics count: " + statsCount);
            System.out.println("Difference: " + (statsCount - actualCount));
            System.out.println("------------------------");
        });
    }

    @Test
    @DisplayName("통계 조회 API와 실제 데이터 비교")
    void compareStatisticsQueryWithActualData() {
        // 1. 전체 통계 조회 (조건 없는 케이스)
        SearchCond defaultCond = new SearchCond(null, null, null, null, null);
        ProjectStatisticsResponse apiResponse = projectStatisticsQueryRepository.getProjectStatistics(defaultCond);

        // 2. 실제 데이터 카운트
        long actualTotalCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .count();

        long actualLocalCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() == null)
                .count();

        long actualGithubCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() != null)
                .count();

        long actualProjectUserCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .map(p -> p.getUser().getId())
                .distinct()
                .count();

        // 3. 결과 출력
        System.out.println("=== API Response vs Actual Data ===");
        System.out.println("Total Projects:");
        System.out.println("  API: " + apiResponse.totalProjectCount());
        System.out.println("  Actual: " + actualTotalCount);
        System.out.println("  Difference: " + (apiResponse.totalProjectCount() - actualTotalCount));

        System.out.println("\nLocal Projects:");
        System.out.println("  API: " + apiResponse.localProjectCount());
        System.out.println("  Actual: " + actualLocalCount);
        System.out.println("  Difference: " + (apiResponse.localProjectCount() - actualLocalCount));

        System.out.println("\nGithub Projects:");
        System.out.println("  API: " + apiResponse.githubProjectCount());
        System.out.println("  Actual: " + actualGithubCount);
        System.out.println("  Difference: " + (apiResponse.githubProjectCount() - actualGithubCount));

        System.out.println("\nProject Users:");
        System.out.println("  API: " + apiResponse.projectUserCount());
        System.out.println("  Actual: " + actualProjectUserCount);
        System.out.println("  Difference: " + (apiResponse.projectUserCount() - actualProjectUserCount));

        // 4. 학과별 테스트
        List<Department> departments = departmentJpaRepository.findAll();
        System.out.println("\n=== Department Statistics ===");
        for (Department dept : departments) {
            SearchCond deptCond = new SearchCond(null, dept.getId(), null, null, null);
            ProjectStatisticsResponse deptResponse = projectStatisticsQueryRepository.getProjectStatistics(deptCond);

            long deptActualCount = projectRepository.findAll().stream()
                    .filter(p -> p.getState() == ACTIVE)
                    .filter(p -> p.getUser().getUserDepartments().stream()
                            .anyMatch(ud -> ud.getDepartment().getId().equals(dept.getId())))
                    .count();

            System.out.println("\nDepartment " + dept.getId() + ":");
            System.out.println("  API: " + deptResponse.totalProjectCount());
            System.out.println("  Actual: " + deptActualCount);
            System.out.println("  Difference: " + (deptResponse.totalProjectCount() - deptActualCount));
        }

        // 5. 학기별 테스트
        List<Semester> semesters = semesterJpaRepository.findAll();
        System.out.println("\n=== Semester Statistics ===");
        for (Semester sem : semesters) {
            SearchCond semCond = new SearchCond(null, null, null, sem.getId(), null);
            ProjectStatisticsResponse semResponse = projectStatisticsQueryRepository.getProjectStatistics(semCond);

            long semActualCount = projectRepository.findAll().stream()
                    .filter(p -> p.getState() == ACTIVE)
                    .filter(p -> p.getSemester() != null && p.getSemester().getId().equals(sem.getId()))
                    .count();

            System.out.println("\nSemester " + sem.getId() + ":");
            System.out.println("  API: " + semResponse.totalProjectCount());
            System.out.println("  Actual: " + semActualCount);
            System.out.println("  Difference: " + (semResponse.totalProjectCount() - semActualCount));
        }
    }
}

package inha.git.statistics.api.service;

import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.statistics.domain.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticsValidationTest {
    @Autowired
    private ProjectJpaRepository projectRepository;

    @Autowired
    private ProjectStatisticsQueryRepository projectStatisticsQueryRepository;

    @Autowired
    private StatisticsJpaRepository statisticsJpaRepository;

    @Autowired
    private DepartmentJpaRepository departmentJpaRepository;

    @Autowired
    private SemesterJpaRepository semesterJpaRepository;


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

    @Test
    @DisplayName("통계 마이그레이션 검증")
    void validateStatisticsMigration() {
        // 1. 전체 통계 검증
        validateTotalStatistics();

        // 2. 사용자별 통계 검증
        validateUserStatistics();

        // 3. 학과별 통계 검증
        validateDepartmentStatistics();

        // 4. 단과대별 통계 검증
        validateCollegeStatistics();

        // 5. 학기/분야/카테고리별 검증
        validateDimensionStatistics();
    }

    private void validateTotalStatistics() {
        // 전체 통계 조회 - findAll()로 변경하고 필터링
        List<Statistics> totalStatsList = statisticsJpaRepository.findByStatisticsTypeAndTargetId(StatisticsType.TOTAL, null);

        // 실제 데이터 카운트
        long actualLocalCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() == null)
                .count();

        long actualGithubCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() != null)
                .count();

        // 통계 합계 계산
        long statsLocalCount = totalStatsList.stream()
                .mapToInt(Statistics::getLocalProjectCount)
                .sum();

        long statsGithubCount = totalStatsList.stream()
                .mapToInt(Statistics::getGithubProjectCount)
                .sum();

        // 검증 및 출력
        System.out.println("=== Total Statistics Validation ===");
        System.out.println("Local Projects:");
        System.out.println("  Actual: " + actualLocalCount);
        System.out.println("  Statistics: " + statsLocalCount);
        System.out.println("  Difference: " + (statsLocalCount - actualLocalCount));

        System.out.println("\nGithub Projects:");
        System.out.println("  Actual: " + actualGithubCount);
        System.out.println("  Statistics: " + statsGithubCount);
        System.out.println("  Difference: " + (statsGithubCount - actualGithubCount));

        // 검증
        assertEquals(actualLocalCount, statsLocalCount, "Total local project count mismatch");
        assertEquals(actualGithubCount, statsGithubCount, "Total github project count mismatch");
    }


    private void validateUserStatistics() {
        // 사용자별 실제 프로젝트 수
        Map<Integer, UserProjectCounts> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .collect(Collectors.groupingBy(
                        p -> p.getUser().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream().filter(p -> p.getRepoName() == null).count(),
                                        projects.stream().filter(p -> p.getRepoName() != null).count()
                                )
                        )
                ));

        // 사용자별 통계 조회 및 검증
        System.out.println("\n=== User Statistics Validation ===");
        actualCounts.forEach((userId, counts) -> {
            List<Statistics> userStats = statisticsJpaRepository
                    .findByStatisticsTypeAndTargetId(StatisticsType.USER, userId.longValue());

            long statsLocalCount = userStats.stream()
                    .mapToInt(Statistics::getLocalProjectCount)
                    .sum();
            long statsGithubCount = userStats.stream()
                    .mapToInt(Statistics::getGithubProjectCount)
                    .sum();

            System.out.println("\nUser ID: " + userId);
            System.out.println("Local Projects:");
            System.out.println("  Actual: " + counts.localCount);
            System.out.println("  Statistics: " + statsLocalCount);
            System.out.println("  Difference: " + (statsLocalCount - counts.localCount));
            System.out.println("Github Projects:");
            System.out.println("  Actual: " + counts.githubCount);
            System.out.println("  Statistics: " + statsGithubCount);
            System.out.println("  Difference: " + (statsGithubCount - counts.githubCount));
        });
    }

    private void validateDepartmentStatistics() {
        // 학과별 실제 프로젝트 수
        Map<Integer, UserProjectCounts> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)
                .flatMap(p -> p.getUser().getUserDepartments().stream()
                        .map(ud -> new ProjectDepartment(p, ud.getDepartment().getId())))
                .collect(Collectors.groupingBy(
                        ProjectDepartment::departmentId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream().filter(pd -> pd.project.getRepoName() == null).count(),
                                        projects.stream().filter(pd -> pd.project.getRepoName() != null).count()
                                )
                        )
                ));

        System.out.println("\n=== Department Statistics Validation ===");
        actualCounts.forEach((departmentId, counts) -> {
            List<Statistics> deptStats = statisticsJpaRepository
                    .findByStatisticsTypeAndTargetId(StatisticsType.DEPARTMENT, departmentId.longValue());

            long statsLocalCount = deptStats.stream()
                    .mapToInt(Statistics::getLocalProjectCount)
                    .sum();
            long statsGithubCount = deptStats.stream()
                    .mapToInt(Statistics::getGithubProjectCount)
                    .sum();

            System.out.println("\nDepartment ID: " + departmentId);
            System.out.println("Local Projects:");
            System.out.println("  Actual: " + counts.localCount);
            System.out.println("  Statistics: " + statsLocalCount);
            System.out.println("  Difference: " + (statsLocalCount - counts.localCount));
            System.out.println("Github Projects:");
            System.out.println("  Actual: " + counts.githubCount);
            System.out.println("  Statistics: " + statsGithubCount);
            System.out.println("  Difference: " + (statsGithubCount - counts.githubCount));
        });
    }

    private void validateCollegeStatistics() {
        // 단과대별 실제 프로젝트 수
        Map<Integer, UserProjectCounts> actualCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)  // ACTIVE 상태의 프로젝트만 필터링
                .flatMap(p -> p.getUser().getUserDepartments().stream()
                        .map(ud -> Map.entry(
                                ud.getDepartment().getCollege().getId(),  // 단과대 ID
                                p  // 프로젝트
                        ))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,  // 단과대 ID로 그룹핑
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream()
                                                .filter(entry -> entry.getValue().getRepoName() == null)  // 로컬 프로젝트
                                                .count(),
                                        projects.stream()
                                                .filter(entry -> entry.getValue().getRepoName() != null)  // GitHub 프로젝트
                                                .count()
                                )
                        )
                ));

        System.out.println("\n=== College Statistics Validation ===");
        actualCounts.forEach((collegeId, counts) -> {
            List<Statistics> collegeStats = statisticsJpaRepository
                    .findByStatisticsTypeAndTargetId(StatisticsType.COLLEGE, collegeId.longValue());

            long statsLocalCount = collegeStats.stream()
                    .mapToInt(Statistics::getLocalProjectCount)
                    .sum();
            long statsGithubCount = collegeStats.stream()
                    .mapToInt(Statistics::getGithubProjectCount)
                    .sum();

            System.out.println("\nCollege ID: " + collegeId);
            System.out.println("Local Projects:");
            System.out.println("  Actual: " + counts.localCount);
            System.out.println("  Statistics: " + statsLocalCount);
            System.out.println("  Difference: " + (statsLocalCount - counts.localCount));
            System.out.println("Github Projects:");
            System.out.println("  Actual: " + counts.githubCount);
            System.out.println("  Statistics: " + statsGithubCount);
            System.out.println("  Difference: " + (statsGithubCount - counts.githubCount));
        });
    }

    private void validateDimensionStatistics() {
        // 학기별 검증
        Map<Integer, UserProjectCounts> semesterCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getSemester() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getSemester().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream().filter(p -> p.getRepoName() == null).count(),
                                        projects.stream().filter(p -> p.getRepoName() != null).count()
                                )
                        )
                ));

        System.out.println("\n=== Semester Statistics Validation ===");
        validateDimensionCounts(semesterCounts, "Semester");

        // 분야별 검증
        Map<Integer, UserProjectCounts> fieldCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE)  // 상태가 ACTIVE인 프로젝트 필터링
                .flatMap(p -> p.getProjectFields().stream()
                        .map(pf -> new ProjectField(
                                new ProjectFieldId(pf.getProject().getId(), pf.getField().getId()),
                                pf.getProject(),
                                pf.getField()
                        ))
                )
                .collect(Collectors.groupingBy(
                        pf -> pf.getField().getId(),  // 필드 ID로 그룹화
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream()
                                                .filter(pf -> pf.getProject().getRepoName() == null)  // 로컬 프로젝트 카운트
                                                .count(),
                                        projects.stream()
                                                .filter(pf -> pf.getProject().getRepoName() != null)  // GitHub 프로젝트 카운트
                                                .count()
                                )
                        )
                ));


        System.out.println("\n=== Field Statistics Validation ===");
        validateDimensionCounts(fieldCounts, "Field");

        // 카테고리별 검증
        Map<Integer, UserProjectCounts> categoryCounts = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getCategory() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCategory().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                projects -> new UserProjectCounts(
                                        projects.stream().filter(p -> p.getRepoName() == null).count(),
                                        projects.stream().filter(p -> p.getRepoName() != null).count()
                                )
                        )
                ));

        System.out.println("\n=== Category Statistics Validation ===");
        validateDimensionCounts(categoryCounts, "Category");
    }

    private void validateDimensionCounts(Map<Integer, UserProjectCounts> actualCounts, String dimensionName) {
        actualCounts.forEach((id, counts) -> {
            // 각 차원에 맞는 쿼리 메소드를 사용하여 통계 조회
            List<Statistics> stats;
            switch (dimensionName) {
                case "Semester" -> stats = statisticsJpaRepository.findBySemesterIdAndStatisticsType(id.longValue(), StatisticsType.TOTAL);
                case "Field" -> stats = statisticsJpaRepository.findByFieldIdAndStatisticsType(id.longValue(), StatisticsType.TOTAL);
                case "Category" -> stats = statisticsJpaRepository.findByCategoryIdAndStatisticsType(id.longValue(), StatisticsType.TOTAL);
                default -> stats = Collections.emptyList();
            }

            long statsLocalCount = stats.stream()
                    .mapToInt(Statistics::getLocalProjectCount)
                    .sum();
            long statsGithubCount = stats.stream()
                    .mapToInt(Statistics::getGithubProjectCount)
                    .sum();

            System.out.println("\n" + dimensionName + " ID: " + id);
            System.out.println("Local Projects:");
            System.out.println("  Actual: " + counts.localCount);
            System.out.println("  Statistics: " + statsLocalCount);
            System.out.println("  Difference: " + (statsLocalCount - counts.localCount));
            System.out.println("Github Projects:");
            System.out.println("  Actual: " + counts.githubCount);
            System.out.println("  Statistics: " + statsGithubCount);
            System.out.println("  Difference: " + (statsGithubCount - counts.githubCount));

            // 검증 추가
            assertEquals(counts.localCount, statsLocalCount,
                    String.format("%s ID %d local project count mismatch", dimensionName, id));
            assertEquals(counts.githubCount, statsGithubCount,
                    String.format("%s ID %d github project count mismatch", dimensionName, id));
        });
    }

    private record UserProjectCounts(long localCount, long githubCount) {}
    private record ProjectDepartment(Project project, Integer departmentId) {}

    @Test
    @DisplayName("통계 조회 성능 비교 테스트")
    void compareQueryPerformance() {
        int numberOfTests = 100; // 각 쿼리를 100번씩 실행
        List<Long> actualQueryTimes = new ArrayList<>();
        List<Long> statisticsQueryTimes = new ArrayList<>();

        // 워밍업 실행
        for (int i = 0; i < 10; i++) {
            runActualDataQuery();
            runStatisticsQuery();
        }

        // 실제 테스트 실행
        for (int i = 0; i < numberOfTests; i++) {
            // 실제 데이터 쿼리 시간 측정
            long startTime = System.nanoTime();
            runActualDataQuery();
            long endTime = System.nanoTime();
            actualQueryTimes.add((endTime - startTime) / 1000000); // nano to milliseconds

            // 통계 테이블 쿼리 시간 측정
            startTime = System.nanoTime();
            runStatisticsQuery();
            endTime = System.nanoTime();
            statisticsQueryTimes.add((endTime - startTime) / 1000000);
        }

        // 결과 분석 및 출력
        printPerformanceResults(actualQueryTimes, statisticsQueryTimes);
    }

    private void runActualDataQuery() {
        long actualLocalCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() == null)
                .count();

        long actualGithubCount = projectRepository.findAll().stream()
                .filter(p -> p.getState() == ACTIVE && p.getRepoName() != null)
                .count();
    }

    private void runStatisticsQuery() {
        List<Statistics> totalStatsList = statisticsJpaRepository
                .findByStatisticsTypeAndTargetId(StatisticsType.TOTAL, null);

        long statsLocalCount = totalStatsList.stream()
                .mapToInt(Statistics::getLocalProjectCount)
                .sum();

        long statsGithubCount = totalStatsList.stream()
                .mapToInt(Statistics::getGithubProjectCount)
                .sum();
    }

    private void printPerformanceResults(List<Long> actualQueryTimes, List<Long> statisticsQueryTimes) {
        // 기본 통계 계산
        DoubleSummaryStatistics actualStats = actualQueryTimes.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics statsTableStats = statisticsQueryTimes.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();

        System.out.println("\n=== Performance Comparison Results ===");
        System.out.println("\nActual Data Query:");
        System.out.println("  Average Time: " + String.format("%.2f", actualStats.getAverage()) + "ms");
        System.out.println("  Min Time: " + actualStats.getMin() + "ms");
        System.out.println("  Max Time: " + actualStats.getMax() + "ms");
        System.out.println("  Standard Deviation: " + calculateStdDev(actualQueryTimes) + "ms");

        System.out.println("\nStatistics Table Query:");
        System.out.println("  Average Time: " + String.format("%.2f", statsTableStats.getAverage()) + "ms");
        System.out.println("  Min Time: " + statsTableStats.getMin() + "ms");
        System.out.println("  Max Time: " + statsTableStats.getMax() + "ms");
        System.out.println("  Standard Deviation: " + calculateStdDev(statisticsQueryTimes) + "ms");

        System.out.println("\nPerformance Improvement:");
        double improvement = ((actualStats.getAverage() - statsTableStats.getAverage()) / actualStats.getAverage()) * 100;
        System.out.println("  " + String.format("%.2f", improvement) + "% faster using statistics table");
    }

    private double calculateStdDev(List<Long> times) {
        double mean = times.stream().mapToDouble(Long::doubleValue).average().orElse(0.0);
        double variance = times.stream()
                .mapToDouble(time -> Math.pow(time - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }
}

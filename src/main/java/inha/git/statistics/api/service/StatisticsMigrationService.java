package inha.git.statistics.api.service;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.repository.UserDepartmentJpaRepository;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.statistics.domain.repository.StatisticsJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static inha.git.common.BaseEntity.State.ACTIVE;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatisticsMigrationService {
    private final ProjectJpaRepository projectJpaRepository;
    private final StatisticsJpaRepository statisticsRepository;
    private final UserDepartmentJpaRepository userDepartmentRepository;

    public void migrateProjectStatistics() {
        log.info("Starting statistics migration...");
        statisticsRepository.deleteAll();

        Map<StatisticsKey, Statistics> statisticsMap = new HashMap<>();
        List<Project> projects = projectJpaRepository.findAllByStateOrderById(ACTIVE);

        for (Project project : projects) {
            try {
                Field field = project.getProjectFields().isEmpty() ?
                        null : project.getProjectFields().get(0).getField();

                if (field == null || project.getCategory() == null || project.getSemester() == null) {
                    log.warn("Project {} missing required data: field={}, category={}, semester={}",
                            project.getId(),
                            field != null,
                            project.getCategory() != null,
                            project.getSemester() != null);
                    continue;
                }

                // 1. 전체 통계 업데이트
                updateStatistics(
                        statisticsMap,
                        StatisticsType.TOTAL,
                        null,
                        project.getSemester().getId(),
                        field.getId(),
                        project.getCategory().getId(),
                        project
                );

                // 2. 사용자 통계
                updateStatistics(
                        statisticsMap,
                        StatisticsType.USER,
                        project.getUser().getId(),
                        project.getSemester().getId(),
                        field.getId(),
                        project.getCategory().getId(),
                        project
                );

                // 3. 학과/단과대 통계
                List<UserDepartment> userDepts = userDepartmentRepository
                        .findByUserId(project.getUser().getId())
                        .orElseThrow(() -> new IllegalStateException(
                                "User departments not found for user: " + project.getUser().getId()));

                for (UserDepartment dept : userDepts) {
                    // 학과 통계
                    updateStatistics(
                            statisticsMap,
                            StatisticsType.DEPARTMENT,
                            dept.getDepartment().getId(),
                            project.getSemester().getId(),
                            field.getId(),
                            project.getCategory().getId(),
                            project
                    );

                    // 단과대 통계
                    updateStatistics(
                            statisticsMap,
                            StatisticsType.COLLEGE,
                            dept.getDepartment().getCollege().getId(),
                            project.getSemester().getId(),
                            field.getId(),
                            project.getCategory().getId(),
                            project
                    );
                }
            } catch (Exception e) {
                log.error("Error processing project {}: {}", project.getId(), e.getMessage());
            }
        }

        statisticsRepository.saveAll(statisticsMap.values());
        log.info("Statistics migration completed. Processed {} projects", projects.size());
    }

    private record StatisticsKey(
            StatisticsType type,
            Integer targetId,
            Integer semesterId,
            Integer fieldId,
            Integer categoryId
    ) {}

    private void updateStatistics(
            Map<StatisticsKey, Statistics> statisticsMap,
            StatisticsType type,
            Integer targetId,
            Integer semesterId,
            Integer fieldId,
            Integer categoryId,
            Project project) {

        StatisticsKey key = new StatisticsKey(type, targetId, semesterId, fieldId, categoryId);

        Statistics stats = statisticsMap.computeIfAbsent(key, k ->
                Statistics.builder()
                        .statisticsType(k.type())
                        .targetId(k.targetId())
                        .semesterId(k.semesterId())
                        .fieldId(k.fieldId())
                        .categoryId(k.categoryId())
                        .localProjectCount(0)
                        .githubProjectCount(0)
                        .questionCount(0)
                        .projectParticipationCount(0)
                        .questionParticipationCount(0)
                        .build()
        );

        if (project.getRepoName() != null) {
            stats.incrementGithubProjectCount();
        } else {
            stats.incrementLocalProjectCount();
        }
        stats.incrementProjectParticipation();
    }
}
package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.TotalDepartmentStatistics;
import inha.git.statistics.domain.enums.StatisticsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long>{

    List<Statistics> findByStatisticsTypeAndTargetId(StatisticsType statisticsType, Long targetId);

    List<Statistics> findBySemesterIdAndStatisticsType(Long semesterId, StatisticsType statisticsType);

    List<Statistics> findByFieldIdAndStatisticsType(Long fieldId, StatisticsType statisticsType);

    List<Statistics> findByCategoryIdAndStatisticsType(Long categoryId, StatisticsType statisticsType);
    List<Statistics> findByStatisticsType(StatisticsType statisticsType);
    Optional<Statistics> findByStatisticsTypeAndTargetIdIsNull(StatisticsType statisticsType);

    Optional<Statistics> findByStatisticsTypeAndTargetIdAndSemesterIdAndFieldIdAndCategoryId(StatisticsType statisticsType, Long targetId, Long semesterId, Long fieldId, Long categoryId);
}

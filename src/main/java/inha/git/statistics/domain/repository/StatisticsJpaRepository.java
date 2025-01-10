package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.Statistics;
import inha.git.statistics.domain.TotalDepartmentStatistics;
import inha.git.statistics.domain.enums.StatisticsType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long>{

    List<Statistics> findByStatisticsTypeAndTargetId(StatisticsType statisticsType, Long targetId);

    List<Statistics> findBySemesterIdAndStatisticsType(Long semesterId, StatisticsType statisticsType);

    List<Statistics> findByFieldIdAndStatisticsType(Long fieldId, StatisticsType statisticsType);

    List<Statistics> findByCategoryIdAndStatisticsType(Long categoryId, StatisticsType statisticsType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT s FROM Statistics s WHERE s.statisticsType = :statisticsType AND s.targetId = :targetId AND s.semesterId = :semesterId AND s.fieldId = :fieldId AND s.categoryId = :categoryId")
    Optional<Statistics> findByStatisticsTypeAndTargetIdAndSemesterIdAndFieldIdAndCategoryIdWithPessimisticLock(
            @Param("statisticsType") StatisticsType statisticsType,
            @Param("targetId") Long targetId,
            @Param("semesterId") Long semesterId,
            @Param("fieldId") Long fieldId,
            @Param("categoryId") Long categoryId
    );
}

package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.TotalUserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TotalUserStatisticsJpaRepository는 사용자별 통계 정보를 조회하는 JPA 레포지토리.
 */
public interface TotalUserStatisticsJpaRepository extends JpaRepository<TotalUserStatistics, Integer>{
}

package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.TotalCollegeStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TotalCollegeStatisticsJpaRepository는 단과대별 통계 정보를 조회하는 JPA 레포지토리.
 */
public interface TotalCollegeStatisticsJpaRepository extends JpaRepository<TotalCollegeStatistics, Integer>{
}

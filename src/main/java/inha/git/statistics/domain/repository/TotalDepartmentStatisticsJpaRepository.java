package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.TotalDepartmentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TotalDepartmentStatisticsJpaRepository 학과별 통계 정보를 조회하는 JPA 레포지토리.
 */
public interface TotalDepartmentStatisticsJpaRepository extends JpaRepository<TotalDepartmentStatistics, Integer>{
}

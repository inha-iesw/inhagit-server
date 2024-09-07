package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.CollegeStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CollegeStatisticsJpaRepository 인터페이스는 단과대 통계 정보를 조회하는데 사용됨.
 */
public interface CollegeStatisticsJpaRepository extends JpaRepository<CollegeStatistics, Integer>{
}

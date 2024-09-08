package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.DepartmentStatistics;
import inha.git.statistics.domain.id.DepartmentStatisticsId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DepartmentStatisticsJpaRepository 인터페이스는 학과 통계 정보를 조회하는데 사용됨.
 */
public interface DepartmentStatisticsJpaRepository extends JpaRepository<DepartmentStatistics, DepartmentStatisticsId>{
}

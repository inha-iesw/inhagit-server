package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.UserCountStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserCountStatisticsJpaRepository는 인터페이스는 UserCountStatistics 정보를 조회하는데 사용됨.
 */
public interface UserCountStatisticsJpaRepository extends JpaRepository<UserCountStatistics, Integer>{
}

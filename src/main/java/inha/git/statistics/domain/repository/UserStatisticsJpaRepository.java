package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserStatisticsJpaRepository 인터페이스는 유저 통계 정보를 조회하는데 사용됨.
 */
public interface UserStatisticsJpaRepository extends JpaRepository<UserStatistics, Integer> {
}

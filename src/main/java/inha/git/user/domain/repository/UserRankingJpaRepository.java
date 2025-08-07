package inha.git.user.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.user.domain.User;
import inha.git.user.domain.UserRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * UserRankingJpaRepository는 User Ranking 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface UserRankingJpaRepository extends JpaRepository<UserRanking, Integer> {
    Optional<UserRanking> findById(Integer userId);
    Optional<UserRanking> findByUser(User user);
    List<UserRanking> findTop10ByOrderByTotalScoreDesc();
}

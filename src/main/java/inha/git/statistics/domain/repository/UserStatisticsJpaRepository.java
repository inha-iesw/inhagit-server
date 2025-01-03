package inha.git.statistics.domain.repository;

import inha.git.statistics.domain.UserStatistics;
import inha.git.statistics.domain.id.UserStatisticsId;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * UserStatisticsJpaRepository 인터페이스는 유저 통계 정보를 조회하는데 사용됨.
 */
public interface UserStatisticsJpaRepository extends JpaRepository<UserStatistics, UserStatisticsId> {
    Optional<List<UserStatistics>> findByUser(User user);

    int countByUserIdAndProjectCountGreaterThan(Integer id, int i);

    int countByUserIdAndGithubProjectCountGreaterThan(Integer id, int i);

    int countByUserIdAndProblemCountGreaterThan(Integer id, int i);

    int countByUserIdAndQuestionCountGreaterThan(Integer id, int i);

    int countByUserIdAndProjectCount(Integer userId, int projectCount);


    int countByUserIdAndGithubProjectCount(Integer userId, int githubProjectCount);

    int countByUserIdAndQuestionCount(Integer userId, int questionCount);


}

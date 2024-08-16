package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.FoundingRecommend;
import inha.git.mapping.domain.id.FoundingRecommendId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * FoundingRecommendJpaRepository는 FoundingRecommend 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface FoundingRecommendJpaRepository extends JpaRepository<FoundingRecommend, FoundingRecommendId> {



    boolean existsByUserAndProject(User user, Project project);

    void deleteByUserAndProject(User user, Project project);
}

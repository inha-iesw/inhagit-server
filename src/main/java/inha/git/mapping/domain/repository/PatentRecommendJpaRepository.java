package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.PatentRecommend;
import inha.git.mapping.domain.id.PatentRecommedId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * PatentRecommendJpaRepository는 PatentRecommend 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface PatentRecommendJpaRepository extends JpaRepository<PatentRecommend, PatentRecommedId> {

    boolean existsByUserAndProject(User user, Project project);
}

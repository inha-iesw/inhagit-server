package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.RegistrationRecommend;
import inha.git.mapping.domain.id.RegistrationRecommendId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * RegistrationRecommendJpaRepository는 RegistrationRecommend 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface RegistrationRecommendJpaRepository extends JpaRepository<RegistrationRecommend, RegistrationRecommendId> {


    boolean existsByUserAndProject(User user, Project project);
}

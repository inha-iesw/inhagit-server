package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProjectLike;
import inha.git.mapping.domain.id.ProjectLikeId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;



/**
 * ProjectLikeJpaRepository는 ProjectLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectLikeJpaRepository extends JpaRepository<ProjectLike, ProjectLikeId> {

    boolean existsByUserAndProject(User user, Project project);

    void deleteByUserAndProject(User user, Project project);
}

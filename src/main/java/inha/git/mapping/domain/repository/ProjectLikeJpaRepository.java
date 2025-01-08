package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProjectLike;
import inha.git.mapping.domain.id.ProjectLikeId;
import inha.git.project.domain.Project;
import inha.git.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


/**
 * ProjectLikeJpaRepository는 ProjectLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectLikeJpaRepository extends JpaRepository<ProjectLike, ProjectLikeId> {

    boolean existsByUserAndProject(User user, Project project);

    void deleteByUserAndProject(User user, Project project);


}

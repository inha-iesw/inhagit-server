package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProjectCommentLike;
import inha.git.mapping.domain.id.ProjectCommentLikeId;
import inha.git.project.domain.ProjectComment;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectCommentLikeJpaRepository는 ProjectCommentLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectCommentLikeJpaRepository extends JpaRepository<ProjectCommentLike, ProjectCommentLikeId> {

    boolean existsByUserAndProjectComment(User user, ProjectComment projectComment);

    void deleteByUserAndProjectComment(User user, ProjectComment projectComment);
}

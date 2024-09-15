package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProjectReplyCommentLike;
import inha.git.mapping.domain.id.ProjectReplyCommentLikeId;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectReplyCommentLikeJpaRepository는 ProjectReplyCommentLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectReplyCommentLikeJpaRepository extends JpaRepository<ProjectReplyCommentLike, ProjectReplyCommentLikeId> {

    boolean existsByUserAndProjectReplyComment(User user, ProjectReplyComment projectReplyComment);

    void deleteByUserAndProjectReplyComment(User user, ProjectReplyComment projectReplyComment);
}

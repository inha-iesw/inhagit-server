package inha.git.project.domain.repository;


import inha.git.project.domain.ProjectReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectReplyCommentJpaRepository는 Project 대댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectReplyCommentJpaRepository extends JpaRepository<ProjectReplyComment, Integer> {


}

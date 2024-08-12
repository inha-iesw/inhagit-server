package inha.git.project.domain.repository;


import inha.git.project.domain.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectCommentJpaRepository는 Project 댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectCommentJpaRepository extends JpaRepository<ProjectComment, Integer> {


}

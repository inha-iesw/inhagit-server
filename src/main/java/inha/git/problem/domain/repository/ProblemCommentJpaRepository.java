package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProblemCommentJpaRepository는 Problem 댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemCommentJpaRepository extends JpaRepository<ProblemComment, Integer> {


}

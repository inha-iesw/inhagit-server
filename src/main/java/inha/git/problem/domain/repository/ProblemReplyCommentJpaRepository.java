package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProblemReplyCommentJpaRepository는 Problem 대댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemReplyCommentJpaRepository extends JpaRepository<ProblemReplyComment, Integer> {


}

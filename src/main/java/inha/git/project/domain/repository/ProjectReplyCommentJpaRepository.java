package inha.git.project.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.common.BaseEntity.State;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * ProjectReplyCommentJpaRepository는 Project 대댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectReplyCommentJpaRepository extends JpaRepository<ProjectReplyComment, Integer> {


    Optional<ProjectReplyComment> findByIdAndState(Integer replyCommentIdx, State state);

    boolean existsByProjectCommentAndState(ProjectComment projectComment, State state);

}

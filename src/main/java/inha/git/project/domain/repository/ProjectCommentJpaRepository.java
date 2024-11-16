package inha.git.project.domain.repository;


import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State;


/**
 * ProjectCommentJpaRepository는 Project 댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectCommentJpaRepository extends JpaRepository<ProjectComment, Integer> {

    Optional<ProjectComment> findByIdAndState(Integer commentIdx, State state);

    List<ProjectComment> findAllByProjectAndStateOrderByIdAsc(Project project, State state);

}

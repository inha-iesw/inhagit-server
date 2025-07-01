package inha.git.project.domain.repository;

import inha.git.project.domain.ProjectStar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State;

/**
 * ProjectCommentJpaRepository는 Project 특허 엔티티에 대한 데이터 액세스 기능을 제공.
 */
@Repository
public interface ProjectStarJpaRepository extends JpaRepository<ProjectStar, Integer> {

    Optional<ProjectStar> findByIdAndState(Integer projectId, State state);

    Optional<ProjectStar> findByProject_IdAndState(Integer projectId, State state);

    Optional<ProjectStar> findByProject_Id(Integer projectId);

    List<ProjectStar> findAllByState(State state);

    List<ProjectStar> findAllByStateOrderByCreatedAtDesc(State state);

    Page<ProjectStar> findByAcceptAtIsNotNullAndStateOrderByCreatedAtDesc(State state, Pageable pageable);

    void deleteByProject_Id(Integer projectId);
}

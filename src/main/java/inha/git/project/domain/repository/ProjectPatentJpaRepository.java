package inha.git.project.domain.repository;

import inha.git.common.BaseEntity;
import inha.git.field.domain.Field;
import inha.git.project.domain.ProjectPatent;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static inha.git.common.BaseEntity.State;

/**
 * ProjectCommentJpaRepository는 Project 특허 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectPatentJpaRepository extends JpaRepository<ProjectPatent, Integer> {
    Optional<ProjectPatent> findByIdAndState(Integer projectPatentId, State state);
    Optional<ProjectPatent> findByApplicationNumberAndState(String applicationNumber, State state);

    long countByProject_UserAndProject_SemesterAndProject_ProjectFields_FieldAndProject_State(User user, Semester semester, Field field, State state);
}

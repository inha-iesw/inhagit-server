package inha.git.project.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.project.domain.Project;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * ProjectJpaRepository는 Project 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Integer> {


    Optional<Project> findByIdAndState(Integer projectIdx, BaseEntity.State state);

    long countByUserAndSemesterAndProjectFields_FieldAndState(User user, Semester semester, Field field, BaseEntity.State state);
}

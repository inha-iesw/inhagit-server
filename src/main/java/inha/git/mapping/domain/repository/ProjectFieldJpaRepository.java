package inha.git.mapping.domain.repository;


import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.domain.Project;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


/**
 * ProjectFieldJpaRepository는 ProjectField엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectFieldJpaRepository extends JpaRepository<ProjectField, ProjectFieldId> {


    @Modifying
    @Query("DELETE FROM ProjectField pf WHERE pf.project = :project")
    void deleteByProject(@Param("project") Project project);
    List<ProjectField> findByProject(Project project);


    Optional<ProjectField> findByProjectAndFieldId(Project project, Integer fieldId);

}

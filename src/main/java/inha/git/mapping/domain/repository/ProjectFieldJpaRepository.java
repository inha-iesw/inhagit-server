package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectFieldJpaRepository는 ProjectField엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectFieldJpaRepository extends JpaRepository<ProjectField, ProjectFieldId> {


}

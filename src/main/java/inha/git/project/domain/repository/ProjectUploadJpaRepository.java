package inha.git.project.domain.repository;

import inha.git.common.BaseEntity;
import inha.git.project.domain.ProjectUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ProjectUploadJpaRepository는 Project 업로드 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectUploadJpaRepository extends JpaRepository<ProjectUpload, Integer> {
    Optional<ProjectUpload> findByProjectIdAndState(Integer projectIdx, BaseEntity.State state);
}

package inha.git.project.domain.repository;


import inha.git.project.domain.ProjectPatent;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectCommentJpaRepository는 Project 특허 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectPatentJpaRepository extends JpaRepository<ProjectPatent, Integer> {


}

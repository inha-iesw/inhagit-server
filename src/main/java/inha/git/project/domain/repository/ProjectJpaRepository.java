package inha.git.project.domain.repository;


import inha.git.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectJpaRepository는 Project 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Integer> {


}

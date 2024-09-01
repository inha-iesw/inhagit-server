package inha.git.project.domain.repository;


import inha.git.project.domain.ProjectPatentInventor;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectPatentInventorJpaRepository는 Project 특허 발명자 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectPatentInventorJpaRepository extends JpaRepository<ProjectPatentInventor, Integer> {


}

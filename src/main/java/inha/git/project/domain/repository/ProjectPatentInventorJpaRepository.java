package inha.git.project.domain.repository;

import inha.git.project.api.controller.dto.response.SearchInventorResponse;
import inha.git.project.domain.ProjectPatent;
import inha.git.project.domain.ProjectPatentInventor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ProjectPatentInventorJpaRepository는 Project 특허 발명자 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectPatentInventorJpaRepository extends JpaRepository<ProjectPatentInventor, Integer> {
    List<SearchInventorResponse> findByProjectPatentId(Integer projectPatentId);

    void deleteAllByProjectPatent(ProjectPatent projectPatent);

    List<ProjectPatentInventor> findAllByProjectPatentOrderByMainInventorDesc(ProjectPatent projectPatent);
}

package inha.git.project.domain.repository;

import inha.git.project.api.controller.dto.response.SearchInventorResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ProjectTeamMemberJpaRepository는 Project 팀원 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectTeamMemberJpaRepository extends JpaRepository<ProjectTeamMember, Integer> {
    List<ProjectTeamMember> findByProject(Project project);

    void deleteAllByProject(Project project);

    List<ProjectTeamMember> findAllByProjectOrderByIdDesc(Project project);
}

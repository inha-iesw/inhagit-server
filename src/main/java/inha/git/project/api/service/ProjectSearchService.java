package inha.git.project.api.service;

import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.api.controller.dto.response.SearchProjectResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectSearchService {

    Page<SearchProjectsResponse> getProjects(Integer page);
    SearchProjectResponse getProject(User user, Integer projectIdx);

    List<SearchFileResponse> getProjectFileByIdx(Integer projectIdx, String path);
}

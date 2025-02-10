package inha.git.project.api.service.query;

import inha.git.project.api.controller.dto.request.SearchProjectCond;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.api.controller.dto.response.SearchProjectResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectQueryService {
    Page<SearchProjectsResponse> getCondProjects(SearchProjectCond searchProjectCond, Integer page, Integer size);
    SearchProjectResponse getProject(User user, Integer projectIdx);
    List<SearchFileResponse> getProjectFileByIdx(User user, Integer projectIdx, String path);
}

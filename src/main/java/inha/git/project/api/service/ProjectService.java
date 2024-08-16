package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.api.controller.api.response.SearchProjectsResponse;
import inha.git.project.api.controller.api.response.UpdateProjectResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {
    Page<SearchProjectsResponse> getProjects(Integer page);
    CreateProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file);
    UpdateProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file);


}

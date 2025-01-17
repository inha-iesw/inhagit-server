package inha.git.project.api.service.command;

import inha.git.project.api.controller.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.dto.request.UpdateProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectCommandService {
    ProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file);
    ProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file);
    ProjectResponse deleteProject(User user, Integer projectIdx);
}

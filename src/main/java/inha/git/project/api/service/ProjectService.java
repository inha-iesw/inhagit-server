package inha.git.project.api.service;

import inha.git.project.api.controller.api.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.api.dto.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.dto.response.ProjectResponse;
import inha.git.project.api.controller.api.response.*;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {

    ProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file);
    ProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file);
    ProjectResponse deleteProject(User user, Integer projectIdx);


}

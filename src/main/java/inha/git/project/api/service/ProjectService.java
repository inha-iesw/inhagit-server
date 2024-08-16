package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.request.RecommendRequest;
import inha.git.project.api.controller.api.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.response.*;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {

    CreateProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file);
    UpdateProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file);
    DeleteProjectResponse deleteProject(User user, Integer projectIdx);


}

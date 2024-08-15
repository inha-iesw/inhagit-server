package inha.git.project.api.service;

import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {
    CreateProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file);
}

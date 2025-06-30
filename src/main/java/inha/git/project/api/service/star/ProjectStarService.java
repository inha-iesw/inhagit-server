package inha.git.project.api.service.star;

import inha.git.admin.api.controller.dto.request.ProjectStarAcceptRequest;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.dto.request.UpdatePatentRequest;
import inha.git.project.api.controller.dto.response.*;
import inha.git.project.domain.enums.PatentType;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectStarService {
    SearchProjectStarResponses searchProjectStar(User user, Integer projectIdx);
    Page<SearchProjectStarResponses> searchProjectStarPage(Integer pageIndex, Integer size);
    ProjectStarResponses createProjectStar(User user, ProjectStarAcceptRequest projectStarAcceptRequest);
    ProjectStarResponses deleteProjectStar(User user, Integer starIdx);
}


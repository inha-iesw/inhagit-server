package inha.git.project.api.service;

import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.user.domain.User;

public interface ProjectPatentService {

    SearchPatentResponse getPatent(User user, String applicationNumber);
    PatentResponse registerPatent(User user, String applicationNumber);
}
package inha.git.project.api.service;

import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.user.domain.User;

public interface ProjectPatentService {

    SearchPatentResponse getProjectPatent(User user, Integer projectIdx);
    SearchPatentResponse searchProjectPatent(User user, String applicationNumber, Integer projectIdx);
    PatentResponse registerPatent(User user, String applicationNumber, Integer projectIdx);

}

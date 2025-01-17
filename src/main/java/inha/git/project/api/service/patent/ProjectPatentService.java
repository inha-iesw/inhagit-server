package inha.git.project.api.service.patent;

import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectPatentService {

    SearchPatentResponse getProjectPatent(User user, Integer projectIdx);
    SearchPatentResponse searchProjectPatent(User user, String applicationNumber, Integer projectIdx);
    PatentResponse registerPatent(User user, String applicationNumber, Integer projectIdx);
    PatentResponse deletePatent(User user, Integer projectIdx);
    PatentResponse registerManualPatent(User user, Integer projectIdx, CreatePatentRequest createPatentRequest, MultipartFile file);
}

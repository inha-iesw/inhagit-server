package inha.git.project.api.service.patent;

import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.request.UpdatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectPatentService {
    PatentResponse createPatent(User user, CreatePatentRequest createPatentRequest, MultipartFile file);
    PatentResponse updatePatent(User user, Integer patentIdx, UpdatePatentRequest updatePatentRequest, MultipartFile file);
    PatentResponse deletePatent(User user, Integer projectIdx);

   }

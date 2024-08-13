package inha.git.user.api.service;

import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    StudentSignupResponse studentSignup(StudentSignupRequest studentSignupRequest);

    ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest);

    CompanySignupResponse companySignup(CompanySignupRequest companySignupRequest, MultipartFile evidence);
}

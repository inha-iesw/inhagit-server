package inha.git.user.api.service;

import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {

    CompanySignupResponse companySignup(CompanySignupRequest companySignupRequest, MultipartFile evidence);
}

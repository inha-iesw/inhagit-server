package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.AdminDemotionRequest;
import inha.git.admin.api.controller.dto.request.AdminPromotionRequest;
import inha.git.admin.api.controller.dto.request.ProfessorAcceptRequest;
import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import org.springframework.data.domain.Page;

public interface AdminService {

    Page<SearchUserResponse> getAdminUsers(String search, Integer page);
    Page<SearchStudentResponse> getAdminStudents(String search, Integer page);
    Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page);
    Page<SearchCompanyResponse> getAdminCompanies(String search, Integer page);
    String promotion(AdminPromotionRequest adminPromotionRequest);
    String demotion(AdminDemotionRequest adminDemotionRequest);

    String acceptProfessor(ProfessorAcceptRequest professorAcceptRequest);
}


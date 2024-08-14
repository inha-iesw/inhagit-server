package inha.git.admin.api.service;


import inha.git.admin.api.controller.dto.request.*;

public interface AdminApproveService {

    String promotion(AdminPromotionRequest adminPromotionRequest);
    String demotion(AdminDemotionRequest adminDemotionRequest);

    String acceptProfessor(ProfessorAcceptRequest professorAcceptRequest);
    String cancelProfessor(ProfessorCancelRequest professorCancelRequest);
    String acceptCompany(CompanyAcceptRequest companyAcceptRequest);
}

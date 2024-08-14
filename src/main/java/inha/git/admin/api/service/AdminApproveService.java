package inha.git.admin.api.service;


import inha.git.admin.api.controller.dto.request.AdminDemotionRequest;
import inha.git.admin.api.controller.dto.request.AdminPromotionRequest;
import inha.git.admin.api.controller.dto.request.ProfessorAcceptRequest;
import inha.git.admin.api.controller.dto.request.ProfessorCancelRequest;

public interface AdminApproveService {

    String promotion(AdminPromotionRequest adminPromotionRequest);
    String demotion(AdminDemotionRequest adminDemotionRequest);

    String acceptProfessor(ProfessorAcceptRequest professorAcceptRequest);
    String cancelProfessor(ProfessorCancelRequest professorCancelRequest);


}

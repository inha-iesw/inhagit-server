package inha.git.admin.api.service;


import inha.git.admin.api.controller.dto.request.*;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.user.domain.User;

public interface AdminApproveService {
    String promotion(User admin, AdminPromotionRequest adminPromotionRequest);
    String demotion(User admin, AdminDemotionRequest adminDemotionRequest);
    String acceptProfessor(User admin, ProfessorAcceptRequest professorAcceptRequest);
    String cancelProfessor(User admin, ProfessorCancelRequest professorCancelRequest);
    String acceptCompany(User admin, CompanyAcceptRequest companyAcceptRequest);
    String cancelCompany(User admin, CompanyCancelRequest companyCancelRequest);
    String promotionStudent(User admin, AssistantPromotionRequest assistantPromotionRequest);
    String demotionStudent(User admin, AssistantDemotionRequest assistantDemotionRequest);
    String blockUser(User admin, UserBlockRequest userBlockRequest);
    String unblockUser(User admin, UserUnblockRequest userUnblockRequest);
    BugReportResponse changeBugReportState(User user, Integer bugReportId, ChangeBugReportStateRequest changeBugReportStateRequest);
    PatentResponse acceptPatent(User user, PatentAcceptRequest patentAcceptRequest);
    PatentResponse cancelPatent(User user, PatentCancelRequest patentCancelRequest);
}

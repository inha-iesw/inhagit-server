package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.SearchReportCond;
import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import org.springframework.data.domain.Page;

public interface AdminSearchService {

    Page<SearchUserResponse> getAdminUsers(String search, Integer page);
    Page<SearchStudentResponse> getAdminStudents(String search, Integer page);
    Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page);
    Page<SearchCompanyResponse> getAdminCompanies(String search, Integer page);
    inha.git.user.api.controller.dto.response.SearchUserResponse getAdminUser(Integer userIdx);
    Page<SearchReportResponse> getAdminReports(SearchReportCond searchReportCond, Integer page);
    Page<SearchBugReportsResponse> getAdminBugReports(SearchBugReportCond searchBugReportCond, Integer page);
}

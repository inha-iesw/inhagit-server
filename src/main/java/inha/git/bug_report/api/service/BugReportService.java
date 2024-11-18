package inha.git.bug_report.api.service;


import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.user.domain.User;


public interface BugReportService {
    BugReportResponse createBugReport(User user, CreateBugReportRequest createBugReportRequest);

}

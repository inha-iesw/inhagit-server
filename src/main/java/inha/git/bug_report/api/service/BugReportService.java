package inha.git.bug_report.api.service;


import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.request.UpdateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportResponse;
import inha.git.user.domain.User;


public interface BugReportService {
    SearchBugReportResponse getBugReport(User user, Integer bugReportId);
    BugReportResponse createBugReport(User user, CreateBugReportRequest createBugReportRequest);
    BugReportResponse updateBugReport(User user, Integer bugReportId, UpdateBugReportRequest updateBugReportRequest);
    BugReportResponse deleteBugReport(User user, Integer bugReportId);


}

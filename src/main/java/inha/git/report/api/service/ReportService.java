package inha.git.report.api.service;

import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportReasonResponse;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface ReportService {
    List<ReportTypeResponse> getReportTypes();
    List<ReportReasonResponse> getReportReasons();
    ReportResponse createReport(User user, CreateReportRequest createReportRequest);
    ReportResponse deleteReport(User user, Integer reportId);
}

package inha.git.report.api.service;

import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.user.domain.User;

public interface ReportService {
    ReportResponse createReport(User user, CreateReportRequest createReportRequest);
}

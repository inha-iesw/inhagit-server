package inha.git.report.api.service;

import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReportServiceImpl은 ReportService 인터페이스를 구현.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportServiceImpl implements ReportService{


    @Override
    public ReportResponse createReport(User user, CreateReportRequest createReportRequest) {
        return null;
    }
}

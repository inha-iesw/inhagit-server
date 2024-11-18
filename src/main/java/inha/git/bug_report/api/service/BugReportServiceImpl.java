package inha.git.bug_report.api.service;

import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.bug_report.api.mapper.BugReportMapper;
import inha.git.bug_report.domain.BugReport;
import inha.git.bug_report.domain.repository.BugReportJpaRepository;
import inha.git.user.domain.User;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * BugReportServiceImpl은 BugReportService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BugReportServiceImpl implements BugReportService {

    private final BugReportJpaRepository bugReportJpaRepository;
    private final BugReportMapper bugReportMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * createBugReport는 버그 제보를 생성하는 메소드.
     * @param user User
     * @param createBugReportRequest CreateBugReportRequest
     * @return BugReportResponse
     */
    @Override
    public BugReportResponse createBugReport(User user, CreateBugReportRequest createBugReportRequest) {
        idempotentProvider.isValidIdempotent(List.of("createBugReportRequest", user.getName(), user.getId().toString(), createBugReportRequest.title()));
        BugReport bugReport = bugReportMapper.createBugReportRequestToBugReport(user, createBugReportRequest);
        BugReport savedBugReport = bugReportJpaRepository.save(bugReport);
        log.info("버그 제보 성공 - 사용자: {} 버그제보  ID: {}", user.getName(), savedBugReport.getId());
        return bugReportMapper.bugReportToBugReportResponse(savedBugReport);
    }
}

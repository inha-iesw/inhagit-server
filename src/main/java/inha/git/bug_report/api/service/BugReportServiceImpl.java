package inha.git.bug_report.api.service;

import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.request.UpdateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportResponse;
import inha.git.bug_report.api.mapper.BugReportMapper;
import inha.git.bug_report.domain.BugReport;
import inha.git.bug_report.domain.repository.BugReportJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.bug_report.domain.enums.BugStatus.CONFIRMING;
import static inha.git.bug_report.domain.enums.BugStatus.UNCONFIRMED;
import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.user.domain.enums.Role.ADMIN;


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
     * getBugReport는 버그 제보를 조회하는 메소드.
     * @param user User
     * @param bugReportId Integer
     * @return SearchBugReportResponse
     */
    @Override
    public SearchBugReportResponse getBugReport(User user, Integer bugReportId) {
        BugReport bugReport = bugReportJpaRepository.findByIdAndState(bugReportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_BUG_REPORT));
        if(!bugReport.getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(FORBIDDEN);
        }
        BugReport savedBugReport = bugReportJpaRepository.save(bugReport);
        SearchUserResponse author = bugReportMapper.userToSearchUserResponse(bugReport.getUser());
        return bugReportMapper.bugReportToSearchBugReportResponse(savedBugReport, author);
    }

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

    /**
     * updateBugReport는 버그 제보를 수정하는 메소드.
     * @param user User
     * @param bugReportId Integer
     * @param updateBugReportRequest UpdateBugReportRequest
     * @return BugReportResponse
     */
    @Override
    public BugReportResponse updateBugReport(User user, Integer bugReportId, UpdateBugReportRequest updateBugReportRequest) {
        idempotentProvider.isValidIdempotent(List.of("updateBugReportRequest", user.getName(), user.getId().toString(), bugReportId.toString(), updateBugReportRequest.title()));

        BugReport bugReport = bugReportJpaRepository.findByIdAndState(bugReportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_BUG_REPORT));
        if(!bugReport.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOT_AUTHORIZED_BUG_REPORT);
        }
        bugReportMapper.updateBugReportRequestToBugReport(bugReport, updateBugReportRequest);
        log.info("버그 제보 수정 성공 - 사용자: {} 버그제보 ID: {}", user.getName(), bugReport.getId());
        return bugReportMapper.bugReportToBugReportResponse(bugReport);
    }

    /**
     * deleteBugReport는 버그 제보를 삭제하는 메소드.
     * @param user User
     * @param bugReportId Integer
     * @return BugReportResponse
     */
    @Override
    public BugReportResponse deleteBugReport(User user, Integer bugReportId) {
        idempotentProvider.isValidIdempotent(List.of("deleteBugReport", user.getName(), user.getId().toString(), bugReportId.toString()));

        BugReport bugReport = bugReportJpaRepository.findByIdAndState(bugReportId, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_BUG_REPORT));
        if(!bugReport.getUser().getId().equals(user.getId()) && !user.getRole().equals(ADMIN)) {
            throw new BaseException(NOT_ALLOWED_DELETE_BUG_REPORT);
        }
        bugReport.setState(INACTIVE);
        bugReport.setDeletedAt();

        log.info("버그 제보 삭제 성공 - 사용자: {} 버그제보 ID: {}", user.getName(), bugReport.getId());
        return bugReportMapper.bugReportToBugReportResponse(bugReport);
    }
}

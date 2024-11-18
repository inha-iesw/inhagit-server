package inha.git.bug_report.api.controller;

import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.request.UpdateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.bug_report.api.service.BugReportService;
import inha.git.common.BaseResponse;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.BUG_REPORT_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.BUG_REPORT_UPDATE_OK;

/**
 * BugReportController는 버그 제보 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "bug-report controller", description = "bug-report 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bug-reports")
public class BugReportController {

    private final BugReportService bannerService;


    /**
     * 버그 제보 API
     *
     * <p>버그를 제보합니다.</p>
     *
     * @param user User
     * @param createBugReportRequest CreateBugReportRequest
     * @return 버그 제보 정보를 포함하는 BaseResponse<BugReportResponse>
     */
    @PostMapping
    @Operation(summary = "버그 제보 API", description = "버그를 제보합니다.")
    public BaseResponse<BugReportResponse> createBugReport(@AuthenticationPrincipal User user,
                                                           @RequestBody @Validated CreateBugReportRequest createBugReportRequest) {
        log.info("버그 제보 요청 - 사용자: {}", user.getName());
        return BaseResponse.of(BUG_REPORT_CREATE_OK, bannerService.createBugReport(user, createBugReportRequest));
    }

    /**
     * 버그 제보 수정 API
     *
     * <p>버그 제보를 수정합니다.</p>
     *
     * @param user User
     * @param bugReportId Integer
     * @param updateBugReportRequest UpdateBugReportRequest
     * @return 수정된 버그 제보 정보를 포함하는 BaseResponse<BugReportResponse>
     */
    @PutMapping("{bugReportId}")
    @Operation(summary = "버그 제보 수정 API", description = "버그 제보를 수정합니다.")
    public BaseResponse<BugReportResponse> updateBugReport(@AuthenticationPrincipal User user,
                                                           @PathVariable("bugReportId") Integer bugReportId,
                                                           @RequestBody @Validated UpdateBugReportRequest updateBugReportRequest) {
        log.info("버그 제보 수정 요청 - 사용자: {}", user.getName());
        return BaseResponse.of(BUG_REPORT_UPDATE_OK, bannerService.updateBugReport(user, bugReportId, updateBugReportRequest));
    }
}

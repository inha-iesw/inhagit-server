package inha.git.report.api.controller;

import inha.git.common.BaseResponse;
import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.api.service.ReportService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.REPORT_CREATE_OK;

/**
 * RepportController는 report 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "report controller", description = "report 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class RepportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고 API", description = "특정 유저를 신고합니다.")
    public BaseResponse<ReportResponse> createReport(@AuthenticationPrincipal User user,
                                                     @RequestBody @Validated CreateReportRequest createReportRequest) {
        return BaseResponse.of(REPORT_CREATE_OK, reportService.createReport(user, createReportRequest));
    }
}

package inha.git.report.api.controller;

import inha.git.common.BaseResponse;
import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.report.api.service.ReportService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.REPORT_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.REPORT_TYPE_GET_OK;

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

    @GetMapping("/reportTypes")
    @Operation(summary = "신고 타입 조회 API", description = "신고 타입을 조회합니다.")
    public BaseResponse<List<ReportTypeResponse>> getReportTypes() {
        return BaseResponse.of(REPORT_TYPE_GET_OK, reportService.getReportTypes());
    }

    /**
     * 신고 API
     *
     * <p>특정 유저를 신고합니다.</p>
     *
     * @param user 사용자
     * @param createReportRequest 신고 생성 요청
     * @return ReportResponse
     */
    @PostMapping
    @Operation(summary = "신고 API", description = "특정 유저를 신고합니다.")
    public BaseResponse<ReportResponse> createReport(@AuthenticationPrincipal User user,
                                                     @RequestBody @Validated CreateReportRequest createReportRequest) {
        return BaseResponse.of(REPORT_CREATE_OK, reportService.createReport(user, createReportRequest));
    }
}

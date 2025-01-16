package inha.git.statistics.api.controller;

import inha.git.common.BaseResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.BatchCollegeStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.api.service.StatisticsExcelService;
import inha.git.statistics.api.service.StatisticsMigrationService;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

@Slf4j
@Tag(name = "statistics controller", description = "statistics 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsExcelService statisticsExcelService;
    private final StatisticsMigrationService statisticsMigrationService;

    /**
     * 프로젝트 통계 조회 API
     *
     * @param searchCond 프로젝트 검색 조건
     * @return BaseResponse<ProjectStatisticsResponse>
     */
    @GetMapping("/project")
    @Operation(summary = "프로젝트 통계 조회 API", description = "프로젝트 통계를 조회합니다.")
    public BaseResponse<ProjectStatisticsResponse> getProjectStatistics(@Validated @ModelAttribute SearchCond searchCond) {
        return BaseResponse.of(PROJECT_STATISTICS_SEARCH_OK, statisticsService.getProjectStatistics(searchCond));
    }

    /**
     * 단과대별 학기별 통계 일괄 조회 API
     *
     * @return BaseResponse<List<BatchCollegeStatisticsResponse>>
     */
    @GetMapping("/batch")
    @Operation(summary = "단과대별 학기별 통계 일괄 조회 API", description = "모든 단과대의 학기별 통계를 한 번에 조회합니다.")
    public BaseResponse<List<BatchCollegeStatisticsResponse>> getBatchStatistics() {
        return BaseResponse.of(BATCH_STATISTICS_SEARCH_OK, statisticsService.getBatchStatistics());
    }

    /**
     * 질문 통계 조회 API
     *
     * @param searchCond 질문 검색 조건
     * @return BaseResponse<QuestionStatisticsResponse>
     */
    @GetMapping("/question")
    @Operation(summary = "질문 통계 조회 API", description = "질문 통계를 조회합니다.")
    public BaseResponse<QuestionStatisticsResponse> getQuestionStatistics(@Validated @ModelAttribute SearchCond searchCond) {
        return BaseResponse.of(QUESTION_STATISTICS_SEARCH_OK, statisticsService.getQuestionStatistics(searchCond));
    }

    /**
     * 엑셀 다운로드 API
     *
     * @param response HttpServletResponse
     * @param statisticsType 통계 타입
     */
    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyAuthority('professor:read', 'admin:read')")
    @Operation(summary = "엑셀 다운로드 API", description = "모든 통계 데이터를 엑셀 파일로 다운로드합니다.")
    public void exportToExcel(@AuthenticationPrincipal User user,  HttpServletResponse response,
                              @RequestParam(value = "statisticsType", defaultValue = "TOTAL") StatisticsType statisticsType,
                              @RequestParam(value = "filterId", required = false) Integer filterId,
                              @RequestParam(value = "semesterId", required = false) Integer semesterId) {
        log.info("엑셀 다운로드 요청: {}", user.getName());
        statisticsExcelService.exportToExcelFile(response, statisticsType, filterId, semesterId);
    }

    /**
     * 통계 마이그레이션 API
     *
     * @return BaseResponse<String>
     */
    @PostMapping("/migration")
    @PreAuthorize("hasAnyAuthority('admin:create', 'admin:update', 'admin:delete')")
    @Operation(summary = "통계 마이그레이션 API", description = "통계 마이그레이션을 수행합니다.")
    public BaseResponse<String> migrateStatistics() {
        statisticsMigrationService.migrateProjectStatistics();
        return BaseResponse.onSuccess("통계 마이그레이션 완료");
    }
}

package inha.git.statistics.api.controller;

import inha.git.common.BaseResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.api.service.StatisticsExcelService;
import inha.git.statistics.api.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static inha.git.common.code.status.SuccessStatus.PROJECT_STATISTICS_SEARCH_OK;
import static inha.git.common.code.status.SuccessStatus.QUESTION_STATISTICS_SEARCH_OK;

@Slf4j
@Tag(name = "statistics controller", description = "statistics 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsExcelService statisticsExcelService;


    /**
     * 학과별 전체 통계 조회 API
     *
     * @return BaseResponse<List<HomeStatisticsResponse>>
     */
//    @GetMapping
//    @Operation(summary = "학과별 전체 통계 조회 API", description = "학과별 전체 통계를 조회합니다.")
//    public BaseResponse<List<HomeStatisticsResponse>> getStatistics() {
//        return BaseResponse.of(DEPARTMENT_STATISTICS_SEARCH_OK, statisticsService.getStatistics());
//    }


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
     */
    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyAuthority('professor:read', 'admin:read')")
    @Operation(summary = "엑셀 다운로드 API", description = "모든 통계 데이터를 엑셀 파일로 다운로드합니다.")
    public void exportToExcel(HttpServletResponse response)  {
        statisticsExcelService.exportToExcelFile(response);
    }


}

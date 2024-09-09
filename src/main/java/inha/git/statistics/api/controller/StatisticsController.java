package inha.git.statistics.api.controller;

import inha.git.common.BaseResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.*;
import inha.git.statistics.api.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

@Slf4j
@Tag(name = "statistics controller", description = "statistics 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;


    /**
     * 학과별 전체 통계 조회 API
     *
     * @return BaseResponse<List<HomeStatisticsResponse>>
     */
    @GetMapping
    @Operation(summary = "학과별 전체 통계 조회 API", description = "학과별 전체 통계를 조회합니다.")
    public BaseResponse<List<HomeStatisticsResponse>> getStatistics() {
        return BaseResponse.of(DEPARTMENT_STATISTICS_SEARCH_OK, statisticsService.getStatistics());
    }


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

    @GetMapping("/team")
    @Operation(summary = "팀 통계 조회 API", description = "팀 통계를 조회합니다.")
    public BaseResponse<TeamStatisticsResponse> getTeamStatistics(@RequestParam(value = "idx", required = false) Integer idx) {
        return BaseResponse.of(TEAM_STATISTICS_SEARCH_OK, statisticsService.getTeamStatistics(idx));
    }

    @GetMapping("/problem")
    @Operation(summary = "문제 통계 조회 API", description = "문제 통계를 조회합니다.")
    public BaseResponse<ProblemStatisticsResponse> getProblemStatistics(@RequestParam(value = "idx", required = false) Integer idx) {
        return BaseResponse.of(PROBLEM_STATISTICS_SEARCH_OK, statisticsService.getProblemStatistics(idx));
    }
}

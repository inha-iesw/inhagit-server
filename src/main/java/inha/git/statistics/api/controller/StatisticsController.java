package inha.git.statistics.api.controller;

import inha.git.common.BaseResponse;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.TeamStatisticsResponse;
import inha.git.statistics.api.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.*;

@Slf4j
@Tag(name = "statistics controller", description = "statistics 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;


    /**
     * 프로젝트 통계 조회 API
     *
     * @param idx Integer
     * @return BaseResponse<ProjectStatisticsResponse>
     */
    @GetMapping("/project")
    @Operation(summary = "프로젝트 통계 조회 API", description = "프로젝트 통계를 조회합니다.")
    public BaseResponse<ProjectStatisticsResponse> getProjectStatistics(@RequestParam(value = "idx", required = false) Integer idx) {
        return BaseResponse.of(PROJECT_STATISTICS_SEARCH_OK, statisticsService.getProjectStatistics(idx));
    }

    @GetMapping("/question")
    @Operation(summary = "질문 통계 조회 API", description = "질문 통계를 조회합니다.")
    public BaseResponse<QuestionStatisticsResponse> getQuestionStatistics(@RequestParam(value = "idx", required = false) Integer idx) {
        return BaseResponse.of(QUESTION_STATISTICS_SEARCH_OK, statisticsService.getQuestionStatistics(idx));
    }

    @GetMapping("/team")
    @Operation(summary = "팀 통계 조회 API", description = "팀 통계를 조회합니다.")
    public BaseResponse<TeamStatisticsResponse> getTeamStatistics(@RequestParam(value = "idx", required = false) Integer idx) {
        return BaseResponse.of(TEAM_STATISTICS_SEARCH_OK, statisticsService.getTeamStatistics(idx));
    }
}
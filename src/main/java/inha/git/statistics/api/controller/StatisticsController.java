package inha.git.statistics.api.controller;

import inha.git.common.BaseResponse;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.PROJECT_STATISTICS_SEARCH_OK;

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
}

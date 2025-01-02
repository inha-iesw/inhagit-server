package inha.git.statistics.api.controller.dto.response;

import inha.git.college.controller.dto.response.SearchCollegeResponse;

import java.util.List;

public record BatchCollegeStatisticsResponse(

        SearchCollegeResponse college,
        List<SemesterStatistics> semesterStatistics
) {
}

package inha.git.statistics.api.controller.dto.response;

public record CollegeStatisticsData(

        Integer collegeId,

        Integer semesterId,

        Integer totalProjectCount,

        Integer localProjectCount,

        Integer githubProjectCount,

        Integer questionCount

) {
}

package inha.git.statistics.api.controller.dto.data;

public record StatisticsCounts(

        Integer totalCount,

        Integer localCount,

        Integer githubCount,

        Integer userCount
) {
}

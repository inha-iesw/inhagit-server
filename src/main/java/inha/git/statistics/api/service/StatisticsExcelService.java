package inha.git.statistics.api.service;

import inha.git.statistics.domain.enums.StatisticsType;
import jakarta.servlet.http.HttpServletResponse;

public interface StatisticsExcelService {

    void exportToExcelFile(HttpServletResponse response, StatisticsType statisticsType, Integer filterId);


}

package inha.git.statistics.api.service;

import jakarta.servlet.http.HttpServletResponse;

public interface StatisticsExcelService {

    void exportToExcelFile(HttpServletResponse response);


}

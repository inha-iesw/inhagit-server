package inha.git.statistics.api.service;

import inha.git.statistics.domain.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionExcelService extends AbstractExcelService{
    @Override
    protected String getSheetName() {
        return null;
    }

    @Override
    protected String getFileName() {
        return null;
    }

    @Override
    protected String[] getHeaders() {
        return new String[0];
    }

    @Override
    protected List<?> getData(StatisticsType statisticsType, Integer filterId, Integer semesterId) {
        return null;
    }

    @Override
    protected void writeData(Sheet sheet, List<?> data) {

    }
}

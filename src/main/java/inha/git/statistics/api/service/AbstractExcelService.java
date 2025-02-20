package inha.git.statistics.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.statistics.domain.enums.StatisticsType;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.EXCEL_CREATE_ERROR;

@Slf4j
public abstract class AbstractExcelService implements StatisticsExcelService {

    @Override
    public void exportToExcelFile(HttpServletResponse response, StatisticsType statisticsType,
                                  Integer filterId, Integer semesterId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(getSheetName());

            // 1. 헤더 생성
            Row headerRow = sheet.createRow(0);
            createHeaders(headerRow, workbook);

            // 2. 데이터 조회 및 작성
            List<?> data = getData(statisticsType, filterId, semesterId);
            writeData(sheet, data);

            // 3. 마무리 작업
            setColumnWidths(sheet);
            writeToResponse(workbook, response);
        } catch (IOException e) {
            throw new BaseException(EXCEL_CREATE_ERROR);
        }
    }

    // 공통 구현 메서드들
    protected void createHeaders(Row headerRow, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        String[] headers = getHeaders();

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    protected CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    protected void setColumnWidths(Sheet sheet) {
        for (int i = 0; i < getHeaders().length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    protected void writeToResponse(Workbook workbook, HttpServletResponse response) throws IOException {
        String fileName = getFileName() + '-' +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) +
                ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }

    protected abstract String getSheetName();
    protected abstract String getFileName();
    protected abstract String[] getHeaders();
    protected abstract List<?> getData(StatisticsType statisticsType, Integer filterId, Integer semesterId);
    protected abstract void writeData(Sheet sheet, List<?> data);
}

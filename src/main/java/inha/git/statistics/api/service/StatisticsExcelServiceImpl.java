package inha.git.statistics.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.ProjectField;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.user.domain.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.EXCEL_CREATE_ERROR;

/**
 * StatisticsExcelServiceImpl은 통계 엑셀 관련 비즈니스 로직을 처리한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsExcelServiceImpl implements StatisticsExcelService {

    private final ProjectJpaRepository projectJpaRepository;
    private static final String OSS_PROJECT_URL = "https://oss.inha.ac.kr/project/detail/";

    @Override
    public void exportToExcelFile(HttpServletResponse response, StatisticsType statisticsType, Integer filterId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("프로젝트 통계");

            Row headerRow = sheet.createRow(0);
            createHeaders(headerRow);

            List<Project> projects = getProjectsByStatisticsType(statisticsType, filterId);
            writeProjectData(sheet, projects);
            setColumnWidths(sheet);
            writeToResponse(workbook, response);

        } catch (IOException e) {
            throw new BaseException(EXCEL_CREATE_ERROR);
        }
    }

    private void createHeaders(Row headerRow) {
        String[] headers = {
                "순번", "단과대", "학과", "학번", "이름", "프로젝트 제목",
                "학기", "카테고리", "분야", "저장소 타입", "링크", "생성 날짜"
        };

        CellStyle headerStyle = createHeaderStyle(headerRow.getSheet().getWorkbook());

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private List<Project> getProjectsByStatisticsType(StatisticsType type, Integer filterId) {
        List<Project> projects = switch (type) {
            case TOTAL -> projectJpaRepository.findAllByState(ACTIVE);
            case COLLEGE -> projectJpaRepository.findAllByUserCollegeIdAndState(filterId, ACTIVE);
            case DEPARTMENT -> projectJpaRepository.findAllByUserDepartmentIdAndState(filterId, ACTIVE);
            case USER -> projectJpaRepository.findAllByUserIdAndState(filterId, ACTIVE);
        };

        if (!projects.isEmpty()) {
            List<Integer> projectIds = projects.stream()
                    .map(Project::getId)
                    .toList();
            List<ProjectField> projectFields = projectJpaRepository.findProjectFieldsByProjectIds(projectIds);

            Map<Integer, List<ProjectField>> fieldMap = projectFields.stream()
                    .collect(Collectors.groupingBy(pf -> pf.getProject().getId()));

            projects.forEach(project ->
                    project.getProjectFields().addAll(fieldMap.getOrDefault(project.getId(), new ArrayList<>()))
            );
        }
        return projects;
    }

    private void writeProjectData(Sheet sheet, List<Project> projects) {
        int rowNum = 1;
        int sequenceNum = 1;

        for (Project project : projects) {
            User user = project.getUser();

            String collegeName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getCollege().getName())
                    .orElse("");

            String departmentName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getName())
                    .orElse("");

            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            row.createCell(colNum++).setCellValue(sequenceNum++);
            row.createCell(colNum++).setCellValue(collegeName);
            row.createCell(colNum++).setCellValue(departmentName);
            row.createCell(colNum++).setCellValue(user.getUserNumber());
            row.createCell(colNum++).setCellValue(user.getName());
            row.createCell(colNum++).setCellValue(project.getTitle());
            row.createCell(colNum++).setCellValue(project.getSemester().getName());
            row.createCell(colNum++).setCellValue(project.getCategory().getName());
            row.createCell(colNum++).setCellValue(getFieldNames(project));
            row.createCell(colNum++).setCellValue(project.getRepoName() != null ? "Github" : "Local");
            row.createCell(colNum++).setCellValue(getProjectLink(project));
            row.createCell(colNum).setCellValue(
                    project.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
        }
    }

    private String getFieldNames(Project project) {
        return project.getProjectFields().stream()
                .map(projectField -> projectField.getField().getName())
                .collect(Collectors.joining(", "));
    }

    private String getProjectLink(Project project) {
        return project.getRepoName() != null ?
                "https://github.com/" + project.getRepoName() :
                OSS_PROJECT_URL + project.getId();
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private void setColumnWidths(Sheet sheet) {
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeToResponse(Workbook workbook, HttpServletResponse response) throws IOException {
        String fileName = "I-OSS_Statistics_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) +
                ".xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }
    }
}

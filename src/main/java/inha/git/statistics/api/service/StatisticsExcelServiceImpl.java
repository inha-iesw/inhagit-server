package inha.git.statistics.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.department.domain.Department;
import inha.git.department.domain.repository.DepartmentJpaRepository;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.domain.StatisticsType;
import inha.git.statistics.domain.repository.ProjectStatisticsQueryRepository;
import inha.git.statistics.domain.repository.QuestionStatisticsQueryRepository;
import inha.git.statistics.domain.repository.StatisticsExcelQueryRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.EXCEL_CREATE_ERROR;

/**
 * StatisticsExcelServiceImpl은 통계 엑셀 관련 비즈니스 로직을 처리한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatisticsExcelServiceImpl implements StatisticsExcelService {

    private final ProjectStatisticsQueryRepository projectStatisticsQueryRepository;
    private final QuestionStatisticsQueryRepository questionStatisticsQueryRepository;
    private final StatisticsExcelQueryRepository statisticsExcelQueryRepository;
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final DepartmentJpaRepository departmentJpaRepository;

    /**
     * 통계 엑셀 파일을 생성하여 출력한다.
     *
     * @param response HttpServletResponse
     */
    @Override
    public void exportToExcelFile(HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();

        // 기본 데이터 조회
        List<College> colleges = collegeJpaRepository.findAllByState(ACTIVE);
        List<Department> departments = departmentJpaRepository.findAllByState(ACTIVE);
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);
        List<Category> categories = categoryJpaRepository.findAllByState(ACTIVE);


        // 1. 전체 통계 (필터 없음)
        createAllStatisticsSheets(workbook, Collections.emptyList(), StatisticsType.TOTAL,
                semesters, fields, categories);

        // 2. 단과대별 통계
        createAllStatisticsSheets(workbook, colleges.stream()
                        .map(college -> new FilterData(college.getId(), college.getName()))
                        .toList(),
                StatisticsType.COLLEGE,
                semesters, fields, categories);

        // 3. 학과별 통계
        createAllStatisticsSheets(workbook, departments.stream()
                        .map(department -> new FilterData(department.getId(), department.getName()))
                        .toList(),
                StatisticsType.DEPARTMENT,
                semesters, fields, categories);

        response.setCharacterEncoding("UTF-8");

        // 캐시 관련 헤더 추가
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 파일명 인코딩 처리
        String encodedFilename;
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String fileName = "ioss_statistics_" + now.format(formatter) + ".xlsx";
            encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new BaseException(EXCEL_CREATE_ERROR);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

    }

    // 필터 데이터를 담는 레코드
    private record FilterData(Integer id, String name) {}

    // 하나의 통계 유형에 대한 모든 시트 생성
    private void createAllStatisticsSheets(Workbook workbook, List<FilterData> filterDataList,
                                           StatisticsType type, List<Semester> semesters,
                                           List<Field> fields, List<Category> categories) {
        String prefix = type.getPrefix();

        if (type == StatisticsType.TOTAL) {
            if (!statisticsExcelQueryRepository.hasNonZeroStatistics(type, null)) {
                return; // 모든 값이 0이면 시트 생성하지 않음
            }
        } else {
            // 필터된 데이터 중 유효한 데이터만 추출
            filterDataList = filterDataList.stream()
                    .filter(filter -> statisticsExcelQueryRepository.hasNonZeroStatistics(type, filter.id()))
                    .toList();

            if (filterDataList.isEmpty()) {
                return; // 유효한 데이터가 없으면 시트 생성하지 않음
            }
        }
        // 1. 기본 통계
        createBasicStatisticsSheet(workbook, prefix + BASIC_STATISTICS, filterDataList, type);

        // 2. 학기별 통계
        createSemesterStatisticsSheet(workbook, prefix + SEMESTER_STATISTICS, filterDataList, type, semesters);

        // 3. 분야별 통계
        createFieldStatisticsSheet(workbook, prefix + FIELD_STATISTICS, filterDataList, type, fields);

        // 4. 카테고리별 통계
        createCategoryStatisticsSheet(workbook, prefix + CATEGORY_STATISTICS, filterDataList, type, categories);

        // 5. 학기_분야별 통계
        createSemesterFieldStatisticsSheet(workbook, prefix + SEMESTER_STATISTICS + FIELD_STATISTICS, filterDataList, type, semesters, fields);

        // 6. 학기_카테고리별 통계
        createSemesterCategoryStatisticsSheet(workbook, prefix + SEMESTER_STATISTICS + CATEGORY_STATISTICS, filterDataList, type, semesters, categories);

        // 7. 분야_카테고리별 통계
        createFieldCategoryStatisticsSheet(workbook, prefix + FIELD_STATISTICS + CATEGORY_STATISTICS, filterDataList, type, fields, categories);

        // 8. 전체_필터링 통계
        createAllFilterStatisticsSheet(workbook, prefix + TOTAL_FILTERING, filterDataList, type, semesters, fields, categories);
    }

    // 기본 통계 시트 생성
    private void createBasicStatisticsSheet(Workbook workbook, String sheetName,
                                            List<FilterData> filterDataList, StatisticsType type) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);

        // 헤더 설정
        int colIdx = 0;
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(PROJECT_PARTICIPATING_STUDENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(PATENT_PARTICIPATING_STUDENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_PARTICIPATING_STUDENT_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            Row dataRow = sheet.createRow(rowIdx);
            SearchCond cond = new SearchCond(null, null, null, null, null);
            addStatisticsRow(dataRow, null, cond, 0);
        } else {
            for (FilterData filterData : filterDataList) {
                Row dataRow = sheet.createRow(rowIdx++);
                SearchCond cond = new SearchCond(
                        type == StatisticsType.COLLEGE ? filterData.id() : null,
                        type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                        null, null, null
                );
                addStatisticsRow(dataRow, filterData.name(), cond, 0);
            }
        }

        autoSizeColumns(sheet);
    }

    private void createSemesterStatisticsSheet(Workbook workbook, String sheetName,
                                               List<FilterData> filterDataList, StatisticsType type,
                                               List<Semester> semesters) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(SEMESTER);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Semester semester : semesters) {
                Row row = sheet.createRow(rowIdx++);
                SearchCond cond = new SearchCond(null, null, semester.getId(), null, null);
                addSemesterRow(row, null, semester.getName(), cond, 0);
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Semester semester : semesters) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(
                            type == StatisticsType.COLLEGE ? filterData.id() : null,
                            type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                            semester.getId(), null, null
                    );
                    addSemesterRow(row, filterData.name(), semester.getName(), cond, 0);
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createFieldStatisticsSheet(Workbook workbook, String sheetName,
                                            List<FilterData> filterDataList, StatisticsType type,
                                            List<Field> fields) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(FIELD);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Field field : fields) {
                Row row = sheet.createRow(rowIdx++);
                SearchCond cond = new SearchCond(null, null, null, field.getId(), null);
                addFieldRow(row, null, field.getName(), cond, 0);
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Field field : fields) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(
                            type == StatisticsType.COLLEGE ? filterData.id() : null,
                            type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                            null, field.getId(), null
                    );
                    addFieldRow(row, filterData.name(), field.getName(), cond, 0);
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createCategoryStatisticsSheet(Workbook workbook, String sheetName,
                                               List<FilterData> filterDataList, StatisticsType type,
                                               List<Category> categories) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(CATEGORY);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Category category : categories) {
                Row row = sheet.createRow(rowIdx++);
                SearchCond cond = new SearchCond(null, null, null, null, category.getId());
                addCategoryRow(row, null, category.getName(), cond, 0);
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Category category : categories) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(
                            type == StatisticsType.COLLEGE ? filterData.id() : null,
                            type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                            null, null, category.getId()
                    );
                    addCategoryRow(row, filterData.name(), category.getName(), cond, 0);
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createSemesterFieldStatisticsSheet(Workbook workbook, String sheetName,
                                                    List<FilterData> filterDataList, StatisticsType type,
                                                    List<Semester> semesters, List<Field> fields) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(SEMESTER);
        headerRow.createCell(colIdx++).setCellValue(FIELD);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(null, null, semester.getId(), field.getId(), null);
                    addSemesterFieldRow(row, null, semester.getName(), field.getName(), cond, 0);
                }
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Semester semester : semesters) {
                    for (Field field : fields) {
                        Row row = sheet.createRow(rowIdx++);
                        SearchCond cond = new SearchCond(
                                type == StatisticsType.COLLEGE ? filterData.id() : null,
                                type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                                semester.getId(), field.getId(), null
                        );
                        addSemesterFieldRow(row, filterData.name(), semester.getName(), field.getName(), cond, 0);
                    }
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createSemesterCategoryStatisticsSheet(Workbook workbook, String sheetName,
                                                      List<FilterData> filterDataList, StatisticsType type,
                                                      List<Semester> semesters, List<Category> categories) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(SEMESTER);
        headerRow.createCell(colIdx++).setCellValue(CATEGORY);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Semester semester : semesters) {
                for (Category category : categories) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(null, null, semester.getId(), null, category.getId());
                    addSemesterCategoryRow(row, null, semester.getName(), category.getName(), cond, 0);
                }
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Semester semester : semesters) {
                    for (Category category : categories) {
                        Row row = sheet.createRow(rowIdx++);
                        SearchCond cond = new SearchCond(
                                type == StatisticsType.COLLEGE ? filterData.id() : null,
                                type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                                semester.getId(), null, category.getId()
                        );
                        addSemesterCategoryRow(row, filterData.name(), semester.getName(), category.getName(), cond, 0);
                    }
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createFieldCategoryStatisticsSheet(Workbook workbook, String sheetName,
                                                    List<FilterData> filterDataList, StatisticsType type,
                                                    List<Field> fields, List<Category> categories) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(FIELD);
        headerRow.createCell(colIdx++).setCellValue(CATEGORY);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Field field : fields) {
                for (Category category : categories) {
                    Row row = sheet.createRow(rowIdx++);
                    SearchCond cond = new SearchCond(null, null, null, field.getId(), category.getId());
                    addFieldCategoryRow(row, null, field.getName(), category.getName(), cond, 0);
                }
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Field field : fields) {
                    for (Category category : categories) {
                        Row row = sheet.createRow(rowIdx++);
                        SearchCond cond = new SearchCond(
                                type == StatisticsType.COLLEGE ? filterData.id() : null,
                                type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                                null, field.getId(), category.getId()
                        );
                        addFieldCategoryRow(row, filterData.name(), field.getName(), category.getName(), cond, 0);
                    }
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void createAllFilterStatisticsSheet(Workbook workbook, String sheetName,
                                                List<FilterData> filterDataList, StatisticsType type,
                                                List<Semester> semesters, List<Field> fields, List<Category> categories) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 설정
        if (type != StatisticsType.TOTAL) {
            headerRow.createCell(colIdx++).setCellValue(type == StatisticsType.COLLEGE ? COLLEGE : DEPARTMENT);
        }
        headerRow.createCell(colIdx++).setCellValue(SEMESTER);
        headerRow.createCell(colIdx++).setCellValue(FIELD);
        headerRow.createCell(colIdx++).setCellValue(CATEGORY);
        headerRow.createCell(colIdx++).setCellValue(TOTAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(LOCAL_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(GITHUB_PROJECT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(REGISTERED_PATENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(PROJECT_PARTICIPATING_STUDENT_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_COUNT);
        headerRow.createCell(colIdx++).setCellValue(QUESTION_PARTICIPATING_STUDENT_COUNT);

        // 데이터 입력
        int rowIdx = 1;
        if (type == StatisticsType.TOTAL) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
                    for (Category category : categories) {
                        Row row = sheet.createRow(rowIdx++);
                        SearchCond cond = new SearchCond(
                                null, null,
                                semester.getId(),
                                field.getId(),
                                category.getId()
                        );
                        addAllFilterRow(row, null, semester.getName(), field.getName(),
                                category.getName(), cond, 0);
                    }
                }
            }
        } else {
            for (FilterData filterData : filterDataList) {
                for (Semester semester : semesters) {
                    for (Field field : fields) {
                        for (Category category : categories) {
                            Row row = sheet.createRow(rowIdx++);
                            SearchCond cond = new SearchCond(
                                    type == StatisticsType.COLLEGE ? filterData.id() : null,
                                    type == StatisticsType.DEPARTMENT ? filterData.id() : null,
                                    semester.getId(),
                                    field.getId(),
                                    category.getId()
                            );
                            addAllFilterRow(row, filterData.name(), semester.getName(),
                                    field.getName(), category.getName(), cond, 0);
                        }
                    }
                }
            }
        }

        autoSizeColumns(sheet);
    }

    private void addAllFilterRow(Row row, String filterName, String semesterName,
                                 String fieldName, String categoryName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(semesterName);
        row.createCell(colIdx++).setCellValue(fieldName);
        row.createCell(colIdx++).setCellValue(categoryName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.projectUserCount());
        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
        row.createCell(colIdx++).setCellValue(questionStats.userCount());
    }


    private void addSemesterRow(Row row, String filterName, String semesterName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(semesterName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
    }

    private void addFieldRow(Row row, String filterName, String fieldName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(fieldName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
    }

    private void addCategoryRow(Row row, String filterName, String categoryName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(categoryName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
    }

    private void addStatisticsRow(Row row, String filterName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.projectUserCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentUserCount());
        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
        row.createCell(colIdx++).setCellValue(questionStats.userCount());
    }

    private void addSemesterFieldRow(Row row, String filterName, String semesterName,
                                     String fieldName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(semesterName);
        row.createCell(colIdx++).setCellValue(fieldName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx).setCellValue(questionStats.questionCount());
    }

    // SemesterCategory 통계용 새로운 메서드
    private void addSemesterCategoryRow(Row row, String filterName, String semesterName,
                                        String categoryName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(semesterName);
        row.createCell(colIdx++).setCellValue(categoryName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx).setCellValue(questionStats.questionCount());
    }

    // FieldCategory 통계용 새로운 메서드
    private void addFieldCategoryRow(Row row, String filterName, String fieldName,
                                     String categoryName, SearchCond cond, int startColIdx) {
        int colIdx = startColIdx;

        if (filterName != null) {
            row.createCell(colIdx++).setCellValue(filterName);
        }
        row.createCell(colIdx++).setCellValue(fieldName);
        row.createCell(colIdx++).setCellValue(categoryName);

        ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
        row.createCell(colIdx).setCellValue(questionStats.questionCount());
    }
    // 열 너비 자동 조정
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }


}

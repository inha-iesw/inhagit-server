package inha.git.statistics.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.domain.repository.ProjectStatisticsQueryRepository;
import inha.git.statistics.domain.repository.QuestionStatisticsQueryRepository;
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
import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
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
    private final CollegeJpaRepository collegeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public void exportToExcelFile(HttpServletResponse response)  {
        Workbook workbook = new XSSFWorkbook();

        // 기본 데이터 조회
        List<College> colleges = collegeJpaRepository.findAllByState(ACTIVE);
        List<Semester> semesters = semesterJpaRepository.findAllByState(ACTIVE);
        List<Field> fields = fieldJpaRepository.findAllByState(ACTIVE);
        List<Category> categories = categoryJpaRepository.findAllByState(ACTIVE);

        // 1. 기본 통계 시트
        Sheet basicSheet = workbook.createSheet("기본 통계");
        createBasicStatisticsSheet(basicSheet, colleges);

        Sheet semesterSheet = workbook.createSheet("학기별");
        createSemesterStatisticsSheet(semesterSheet, colleges, semesters);

        Sheet fieldSheet = workbook.createSheet("분야별");
        createFieldStatisticsSheet(fieldSheet, colleges, fields);

        Sheet categorySheet = workbook.createSheet("카테고리별");
        createCategoryStatisticsSheet(categorySheet, colleges, categories);

        // 2. 학기별 분야별 통계 시트
        Sheet semesterFieldSheet = workbook.createSheet("학기_분야별");
        createSemesterFieldStatisticsSheet(semesterFieldSheet, colleges, semesters, fields);

        // 3. 학기별 카테고리별 통계 시트
        Sheet semesterCategorySheet = workbook.createSheet("학기_카테고리별");
        createSemesterCategoryStatisticsSheet(semesterCategorySheet, colleges, semesters, categories);

        // 4. 분야별 카테고리별 통계 시트
        Sheet fieldCategorySheet = workbook.createSheet("분야_카테고리별");
        createFieldCategoryStatisticsSheet(fieldCategorySheet, colleges, fields, categories);

        // 5. 학기별 분야별 카테고리별 통계 시트
        Sheet allFilterSheet = workbook.createSheet("전체_필터링");
        createAllFilterStatisticsSheet(allFilterSheet, colleges, semesters, fields, categories);

        // Content-Type과 header 설정
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=detailed_college_statistics.xlsx");

        // 엑셀 파일 출력
        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            log.error("엑셀 파일 생성 중 오류 발생", e);
            throw new BaseException(EXCEL_CREATE_ERROR);
        }
    }

    private void createBasicStatisticsSheet(Sheet sheet, List<College> colleges) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("참여 학생 수");
        headerRow.createCell(colIdx++).setCellValue("특허 참여 학생 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");
        headerRow.createCell(colIdx++).setCellValue("질문 참여 학생 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            Row row = sheet.createRow(rowIdx++);
            colIdx = 0;

            SearchCond cond = new SearchCond(college.getId(), null, null, null, null);
            ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
            QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

            row.createCell(colIdx++).setCellValue(college.getName());
            row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
            row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
            row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
            row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
            row.createCell(colIdx++).setCellValue(projectStats.projectUserCount());
            row.createCell(colIdx++).setCellValue(projectStats.patentUserCount());
            row.createCell(colIdx++).setCellValue(questionStats.questionCount());
            row.createCell(colIdx++).setCellValue(questionStats.userCount());
        }

        autoSizeColumns(sheet);
    }


    // 학기별 통계 시트 생성
    private void createSemesterStatisticsSheet(Sheet sheet, List<College> colleges, List<Semester> semesters) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("학기");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");


        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Semester semester : semesters) {
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;

                SearchCond cond = new SearchCond(college.getId(), null, semester.getId(), null, null);
                ProjectStatisticsResponse projectStats = projectStatisticsQueryRepository.getProjectStatistics(cond);
                QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                row.createCell(colIdx++).setCellValue(college.getName());
                row.createCell(colIdx++).setCellValue(semester.getName());
                row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                row.createCell(colIdx++).setCellValue(questionStats.questionCount());

            }
        }

        autoSizeColumns(sheet);
    }

    // 분야별 통계 시트 생성
    private void createFieldStatisticsSheet(Sheet sheet, List<College> colleges, List<Field> fields) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("분야");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");


        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Field field : fields) {
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;

                SearchCond cond = new SearchCond(college.getId(), null, null, field.getId(), null);
                ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                row.createCell(colIdx++).setCellValue(college.getName());
                row.createCell(colIdx++).setCellValue(field.getName());
                row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                row.createCell(colIdx++).setCellValue(questionStats.questionCount());

            }
        }

        autoSizeColumns(sheet);
    }

    // 카테고리별 통계 시트 생성
    private void createCategoryStatisticsSheet(Sheet sheet, List<College> colleges, List<Category> categories) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("카테고리");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Category category : categories) {
                Row row = sheet.createRow(rowIdx++);
                colIdx = 0;

                SearchCond cond = new SearchCond(college.getId(), null, null, null, category.getId());
                ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                row.createCell(colIdx++).setCellValue(college.getName());
                row.createCell(colIdx++).setCellValue(category.getName());
                row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                row.createCell(colIdx++).setCellValue(questionStats.questionCount());
            }
        }

        autoSizeColumns(sheet);
    }

    // 학기별 분야별 통계 시트 생성
    private void createSemesterFieldStatisticsSheet(Sheet sheet, List<College> colleges,
                                                    List<Semester> semesters, List<Field> fields) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("학기");
        headerRow.createCell(colIdx++).setCellValue("분야");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
                    Row row = sheet.createRow(rowIdx++);
                    colIdx = 0;

                    SearchCond cond = new SearchCond(college.getId(), null, semester.getId(), field.getId(), null);
                    ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                    QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                    row.createCell(colIdx++).setCellValue(college.getName());
                    row.createCell(colIdx++).setCellValue(semester.getName());
                    row.createCell(colIdx++).setCellValue(field.getName());
                    row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                    row.createCell(colIdx++).setCellValue(questionStats.questionCount());
                }
            }
        }

        autoSizeColumns(sheet);
    }

    // 학기별 카테고리별 통계 시트 생성
    private void createSemesterCategoryStatisticsSheet(Sheet sheet, List<College> colleges,
                                                       List<Semester> semesters, List<Category> categories) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("학기");
        headerRow.createCell(colIdx++).setCellValue("카테고리");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Semester semester : semesters) {
                for (Category category : categories) {
                    Row row = sheet.createRow(rowIdx++);
                    colIdx = 0;

                    SearchCond cond = new SearchCond(college.getId(), null, semester.getId(), null, category.getId());
                    ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                    QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                    row.createCell(colIdx++).setCellValue(college.getName());
                    row.createCell(colIdx++).setCellValue(semester.getName());
                    row.createCell(colIdx++).setCellValue(category.getName());
                    row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                    row.createCell(colIdx++).setCellValue(questionStats.questionCount());
                }
            }
        }

        autoSizeColumns(sheet);
    }

    // 분야별 카테고리별 통계 시트 생성
    private void createFieldCategoryStatisticsSheet(Sheet sheet, List<College> colleges,
                                                    List<Field> fields, List<Category> categories) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("분야");
        headerRow.createCell(colIdx++).setCellValue("카테고리");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Field field : fields) {
                for (Category category : categories) {
                    Row row = sheet.createRow(rowIdx++);
                    colIdx = 0;

                    SearchCond cond = new SearchCond(college.getId(), null, null, field.getId(), category.getId());
                    ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                    QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                    row.createCell(colIdx++).setCellValue(college.getName());
                    row.createCell(colIdx++).setCellValue(field.getName());
                    row.createCell(colIdx++).setCellValue(category.getName());
                    row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                    row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                    row.createCell(colIdx++).setCellValue(questionStats.questionCount());
                }
            }
        }

        autoSizeColumns(sheet);
    }

    // 전체 필터링 통계 시트 생성 (학기 + 분야 + 카테고리)
    private void createAllFilterStatisticsSheet(Sheet sheet, List<College> colleges,
                                                List<Semester> semesters, List<Field> fields, List<Category> categories) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        // 헤더 생성
        headerRow.createCell(colIdx++).setCellValue("단과대");
        headerRow.createCell(colIdx++).setCellValue("학기");
        headerRow.createCell(colIdx++).setCellValue("분야");
        headerRow.createCell(colIdx++).setCellValue("카테고리");
        headerRow.createCell(colIdx++).setCellValue("전체 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("로컬 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("깃허브 프로젝트");
        headerRow.createCell(colIdx++).setCellValue("특허 수");
        headerRow.createCell(colIdx++).setCellValue("참여 학생 수");
        headerRow.createCell(colIdx++).setCellValue("질문 수");
        headerRow.createCell(colIdx++).setCellValue("질문 참여 학생 수");

        // 데이터 입력
        int rowIdx = 1;
        for (College college : colleges) {
            for (Semester semester : semesters) {
                for (Field field : fields) {
                    for (Category category : categories) {
                        Row row = sheet.createRow(rowIdx++);
                        colIdx = 0;

                        SearchCond cond = new SearchCond(college.getId(), null, semester.getId(), field.getId(), category.getId());
                        ProjectStatisticsResponse projectStats  = projectStatisticsQueryRepository.getProjectStatistics(cond);
                        QuestionStatisticsResponse questionStats = questionStatisticsQueryRepository.getQuestionStatistics(cond);

                        row.createCell(colIdx++).setCellValue(college.getName());
                        row.createCell(colIdx++).setCellValue(semester.getName());
                        row.createCell(colIdx++).setCellValue(field.getName());
                        row.createCell(colIdx++).setCellValue(category.getName());
                        row.createCell(colIdx++).setCellValue(projectStats.totalProjectCount());
                        row.createCell(colIdx++).setCellValue(projectStats.localProjectCount());
                        row.createCell(colIdx++).setCellValue(projectStats.githubProjectCount());
                        row.createCell(colIdx++).setCellValue(projectStats.patentProjectCount());
                        row.createCell(colIdx++).setCellValue(projectStats.projectUserCount());
                        row.createCell(colIdx++).setCellValue(questionStats.questionCount());
                        row.createCell(colIdx++).setCellValue(questionStats.userCount());
                    }
                }
            }
        }

        autoSizeColumns(sheet);
    }

    // 열 너비 자동 조정
    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

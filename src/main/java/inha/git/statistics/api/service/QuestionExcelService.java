package inha.git.statistics.api.service;

import inha.git.question.domain.Question;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionExcelService extends AbstractExcelService {

    private final QuestionJpaRepository questionJpaRepository;

    private static final String[] HEADERS = {
            "순번", "단과대", "학과", "학번", "이름", "I-SSS 제목",
            "학기", "카테고리", "분야", "링크", "업로드 날짜"
    };

    @Override
    protected String getSheetName() {
        return "I-SSS_통계";
    }

    @Override
    protected String getFileName() {
        return "I-SSS_통계";
    }

    @Override
    protected String[] getHeaders() {
        return HEADERS;
    }

    @Override
    protected List<?> getData(StatisticsType type, Integer filterId, Integer semesterId) {
        return switch (type) {
            case TOTAL -> questionJpaRepository.findAllQuestions(semesterId, ACTIVE);
            case COLLEGE -> questionJpaRepository.findAllQuestionsByCollege(filterId, semesterId, ACTIVE);
            case DEPARTMENT -> questionJpaRepository.findAllQuestionsByDepartment(filterId, semesterId, ACTIVE);
            case USER -> questionJpaRepository.findAllQuestionsByUser(filterId, semesterId, ACTIVE);
        };
    }

    @Override
    protected void writeData(Sheet sheet, List<?> data) {
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) data;

        int rowNum = 1;
        int sequenceNum = 1;
        int questionCount = 0;

        for (Question question : questions) {
            User user = question.getUser();
            questionCount++;

            String collegeName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getCollege().getName())
                    .orElse("");

            String departmentName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getName())
                    .orElse("");

            String fieldNames = question.getQuestionFields().stream()
                    .map(questionField -> questionField.getField().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));

            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            row.createCell(colNum++).setCellValue(sequenceNum++);
            row.createCell(colNum++).setCellValue(collegeName);
            row.createCell(colNum++).setCellValue(departmentName);
            row.createCell(colNum++).setCellValue(user.getUserNumber());
            row.createCell(colNum++).setCellValue(user.getName());
            row.createCell(colNum++).setCellValue(question.getTitle());
            row.createCell(colNum++).setCellValue(question.getSemester().getName());
            row.createCell(colNum++).setCellValue(question.getCategory().getName());
            row.createCell(colNum++).setCellValue(fieldNames);
            row.createCell(colNum++).setCellValue("https://oss.inha.ac.kr/question/detail/" + question.getId());
            row.createCell(colNum).setCellValue(
                    question.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
        }

        rowNum++;
        sheet.createRow(rowNum++);

        Row summaryRow = sheet.createRow(rowNum);
        summaryRow.createCell(1).setCellValue("멘토링 건수: " + questionCount + "건");
    }
}

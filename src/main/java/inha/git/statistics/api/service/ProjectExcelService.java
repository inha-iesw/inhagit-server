package inha.git.statistics.api.service;

import inha.git.mapping.domain.ProjectField;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.GITHUB_URL;
import static inha.git.common.Constant.OSS_PROJECT_URL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectExcelService extends AbstractExcelService {

    private final ProjectJpaRepository projectJpaRepository;

    private static final String[] HEADERS = {
            "순번", "단과대", "학과", "학번", "이름", "I-FOSS 제목",
            "학기", "카테고리", "분야", "저장소 타입", "링크", "업로드 날짜"
    };

    @Override
    protected String getSheetName() {
        return "I-FOSS 통계";
    }

    @Override
    protected String getFileName() {
        return "I-FOSS_통계";
    }

    @Override
    protected String[] getHeaders() {
        return HEADERS;
    }

    @Override
    protected List<?> getData(StatisticsType type, Integer filterId, Integer semesterId) {
        List<Project> projects = switch (type) {
            case TOTAL -> projectJpaRepository.findAllByState(semesterId, ACTIVE);
            case COLLEGE -> projectJpaRepository.findAllByUserCollegeIdAndState(filterId, semesterId, ACTIVE);
            case DEPARTMENT -> projectJpaRepository.findAllByUserDepartmentIdAndState(filterId, semesterId, ACTIVE);
            case USER -> projectJpaRepository.findAllByUserIdAndState(filterId, semesterId, ACTIVE);
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

    @Override
    protected void writeData(Sheet sheet, List<?> data) {
        @SuppressWarnings("unchecked")
        List<Project> projects = (List<Project>) data;

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
                GITHUB_URL + project.getRepoName() :
                OSS_PROJECT_URL + project.getId();
    }
}

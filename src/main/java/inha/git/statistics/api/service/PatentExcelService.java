package inha.git.statistics.api.service;

import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectPatent;
import inha.git.project.domain.ProjectPatentInventor;
import inha.git.project.domain.enums.PatentType;
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.statistics.domain.enums.StatisticsType;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.*;
import static inha.git.common.Constant.OSS_PROJECT_URL;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatentExcelService extends AbstractExcelService {

    private final ProjectPatentJpaRepository projectPatentJpaRepository;

    private static final String[] HEADERS = {
            "순번", "단과대", "학과", "학번", "이름", "특허종류", "특허 제목", "출원 번호",
            "출원인 이름", "참여자 이름", "참여 기관", "프로젝트 링크", "출원 일자"
    };

    @Override
    protected String getSheetName() {
        return "I-CRM 통계";
    }

    @Override
    protected String getFileName() {
        return "I-CRM_통계";
    }

    @Override
    protected String[] getHeaders() {
        return HEADERS;
    }

    @Override
    protected List<?> getData(StatisticsType type, Integer filterId, Integer semesterId) {
        // 1. 기본 데이터 조회
        List<ProjectPatent> patents = switch (type) {
            case TOTAL -> projectPatentJpaRepository.findAllAcceptedPatents(semesterId, ACTIVE);
            case COLLEGE -> projectPatentJpaRepository.findAllAcceptedPatentsByCollege(filterId, semesterId, ACTIVE);
            case DEPARTMENT -> projectPatentJpaRepository.findAllAcceptedPatentsByDepartment(filterId, semesterId, ACTIVE);
            case USER -> projectPatentJpaRepository.findAllAcceptedPatentsByUser(filterId, semesterId, ACTIVE);
        };

        // 2. inventors 데이터 추가 조회
        if (!patents.isEmpty()) {
            List<ProjectPatent> patentsWithInventors = projectPatentJpaRepository.findPatentsWithInventors(patents);

            // inventors 정보를 원본 객체에 복사
            Map<Integer, List<ProjectPatentInventor>> inventorMap = patentsWithInventors.stream()
                    .collect(Collectors.toMap(
                            ProjectPatent::getId,
                            ProjectPatent::getProjectPatentInventors
                    ));

            patents.forEach(patent ->
                    patent.getProjectPatentInventors().addAll(
                            inventorMap.getOrDefault(patent.getId(), new ArrayList<>())
                    )
            );
        }

        return patents;
    }

    @Override
    protected void writeData(Sheet sheet, List<?> data) {
        @SuppressWarnings("unchecked")
        List<ProjectPatent> patents = (List<ProjectPatent>) data;

        int rowNum = 1;
        int sequenceNum = 1;
        PatentType currentType = null;

        for (ProjectPatent patent : patents) {
            Project project = patent.getProject();
            User user = project.getUser();

            if (currentType != null && currentType != patent.getPatentType()) {
                sheet.createRow(rowNum++);
                sequenceNum = 1;
            }
            currentType = patent.getPatentType();

            String collegeName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getCollege().getName())
                    .orElse("");

            String departmentName = user.getUserDepartments().stream()
                    .findFirst()
                    .map(ud -> ud.getDepartment().getName())
                    .orElse("");

            String inventorNames = patent.getProjectPatentInventors().stream()
                    .map(ProjectPatentInventor::getName)
                    .distinct()
                    .collect(Collectors.joining(", "));

            String inventorAffiliations = patent.getProjectPatentInventors().stream()
                    .map(ProjectPatentInventor::getAffiliation)
                    .distinct()
                    .collect(Collectors.joining(", "));

            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            row.createCell(colNum++).setCellValue(sequenceNum++);
            row.createCell(colNum++).setCellValue(collegeName);
            row.createCell(colNum++).setCellValue(departmentName);
            row.createCell(colNum++).setCellValue(user.getUserNumber());
            row.createCell(colNum++).setCellValue(user.getName());
            row.createCell(colNum++).setCellValue(patent.getPatentType().getDescription());
            row.createCell(colNum++).setCellValue(patent.getInventionTitle());
            row.createCell(colNum++).setCellValue(patent.getApplicationNumber());
            row.createCell(colNum++).setCellValue(patent.getApplicantName());
            row.createCell(colNum++).setCellValue(inventorNames);
            row.createCell(colNum++).setCellValue(inventorAffiliations);
            row.createCell(colNum++).setCellValue(OSS_PROJECT_URL + project.getId());
            row.createCell(colNum).setCellValue(patent.getApplicationDate());
        }
    }
}

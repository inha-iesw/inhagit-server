package inha.git.project.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
import inha.git.project.api.controller.dto.request.CreateGithubProjectRequest;
import inha.git.project.api.controller.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.dto.request.UpdateProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import inha.git.utils.file.FilePath;
import inha.git.utils.file.UnZip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ProjectService는 프로젝트 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectUploadJpaRepository projectUploadJpaRepository;
    private final ProjectFieldJpaRepository projectFieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProjectMapper projectMapper;
    private final StatisticsService statisticsService;
    private final IdempotentProvider idempotentProvider;

    /**
     * 프로젝트 생성
     *
     * @param user                사용자 정보
     * @param createProjectRequest 프로젝트 생성 요청
     * @param file                프로젝트 파일
     * @return 생성된 프로젝트 정보
     */
    @Override
    @Transactional
    public ProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file) {

        idempotentProvider.isValidIdempotent(List.of("createProject", user.getName(), user.getId().toString(), createProjectRequest.title(), createProjectRequest.contents(), createProjectRequest.subject()));

        String[] paths = storeAndUnzipFile(file);
        String zipFilePath = paths[0];
        String folderName = paths[1];
        registerRollbackCleanup(zipFilePath, folderName);

        Semester semester = semesterJpaRepository.findByIdAndState(createProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Category category = categoryJpaRepository.findById(createProjectRequest.categoryIdx())
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));

        Project project = projectMapper.createProjectRequestToProject(createProjectRequest, user, semester, category);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        ProjectUpload projectUpload = projectMapper.createProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, savedProject);
        projectUploadJpaRepository.save(projectUpload);

        List<ProjectField> projectFields = createAndSaveProjectFields(createProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        List<Field> fields = fieldJpaRepository.findAllById(createProjectRequest.fieldIdxList());

        statisticsService.adjustCount(user, fields, semester, category,  1, true);
        log.info("프로젝트 생성 성공 - 사용자: {} 프로젝트 ID: {}", user.getName(), savedProject.getId());
        return projectMapper.projectToProjectResponse(savedProject);
    }

    /**
     * 깃허브 프로젝트 생성
     *
     * @param user                     사용자 정보
     * @param createGithubProjectRequest 깃허브 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @Override
    @Transactional
    public ProjectResponse createGithubProject(User user, CreateGithubProjectRequest createGithubProjectRequest) {
        idempotentProvider.isValidIdempotent(List.of("createGithubProject", user.getName(), user.getId().toString(), createGithubProjectRequest.title(), createGithubProjectRequest.contents(), createGithubProjectRequest.subject()));

        Semester semester = semesterJpaRepository.findByIdAndState(createGithubProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));

        Category category = categoryJpaRepository.findById(createGithubProjectRequest.categoryIdx())
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));

        Project project = projectMapper.createGithubProjectRequestToProject(createGithubProjectRequest, user, semester, category);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        List<ProjectField> projectFields = createAndSaveProjectFields(createGithubProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);
        List<Field> fields = fieldJpaRepository.findAllById(createGithubProjectRequest.fieldIdxList());
        statisticsService.adjustCount(user, fields, semester,  category, 2, true);
        log.info("깃허브 프로젝트 생성 성공 - 사용자: {} 프로젝트 ID: {}", user.getName(), savedProject.getId());
        return projectMapper.projectToProjectResponse(savedProject);
    }


    /**
     * 프로젝트 업데이트
     *
     * @param user                사용자 정보
     * @param projectIdx          프로젝트 인덱스
     * @param updateProjectRequest 프로젝트 업데이트 요청
     * @param file                프로젝트 파일
     * @return 업데이트된 프로젝트 정보
     */
    @Override
    @Transactional
    public ProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file) {
        idempotentProvider.isValidIdempotent(List.of("updateProject", user.getName(), user.getId().toString(), updateProjectRequest.title(), updateProjectRequest.contents(), updateProjectRequest.subject()));

        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        if (!project.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.error("프로젝트 수정 권한이 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_NOT_AUTHORIZED);
        }

        // 변경 전 상태 저장
        Semester originSemester = project.getSemester();
        Category originCategory = project.getCategory();
        List<Field> originFields = project.getProjectFields().stream()
                .map(ProjectField::getField)
                .toList();

        // 새로운 학기 정보 가져오기
        Semester newSemester = semesterJpaRepository.findByIdAndState(updateProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Category newCategory = categoryJpaRepository.findById(updateProjectRequest.categoryIdx())
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));

        // 새로운 필드 정보 처리
        List<Integer> newFieldIds = updateProjectRequest.fieldIdxList();
        List<Field> newFields = fieldJpaRepository.findAllById(newFieldIds);

        // 프로젝트 정보 업데이트
        projectMapper.updateProjectRequestToProject(updateProjectRequest, project, newSemester, newCategory);

        // 필드 정보 업데이트 (디버깅을 위한 로깅 추가)
        Set<Integer> existingFieldIds = project.getProjectFields().stream()
                .map(pf -> pf.getField().getId())
                .collect(Collectors.toSet());


        Set<Integer> newFieldIdSet = new HashSet<>(newFieldIds);

        // 삭제해야 할 분야 처리
        List<Integer> fieldsToRemove = existingFieldIds.stream()
                .filter(id -> !newFieldIdSet.contains(id))
                .toList();


        fieldsToRemove.forEach(id -> {
            ProjectField projectField = project.getProjectFields().stream()
                    .filter(pf -> pf.getField().getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (projectField != null) {
                project.getProjectFields().remove(projectField);
                projectFieldJpaRepository.delete(projectField);
                log.debug("필드 ID {} 삭제됨", id);
            }
        });

        // 새로 추가해야 할 분야 처리
        List<Integer> fieldsToAdd = newFieldIdSet.stream()
                .filter(id -> !existingFieldIds.contains(id))
                .toList();

        fieldsToAdd.forEach(id -> {
            Field field = fieldJpaRepository.findById(id)
                    .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
            ProjectField newProjectField = new ProjectField(new ProjectFieldId(projectIdx, id), project, field);
            project.getProjectFields().add(newProjectField);
            projectFieldJpaRepository.save(newProjectField);
        });

        Project savedProject = projectJpaRepository.saveAndFlush(project);

        savedProject.getProjectFields().stream()
                .map(pf -> pf.getField().getId())
                .toList();

        // 통계 업데이트
        boolean isRepoProject = project.getRepoName() != null;
        int statisticsValue = isRepoProject ? 2 : 1;

        statisticsService.adjustCount(user, originFields, originSemester, originCategory, statisticsValue, false);
        statisticsService.adjustCount(user, newFields, newSemester, newCategory, statisticsValue, true);

        if (project.getRepoName() == null) {
            ProjectUpload findProjectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                    .orElseThrow(() -> new BaseException(PROJECT_UPLOAD_NOT_FOUND));

            String directoryName = findProjectUpload.getDirectoryName();
            String zipDirectoryName = findProjectUpload.getZipDirectoryName();

            if (file != null) {
                String[] paths = storeAndUnzipFile(file);
                String zipFilePath = paths[0];
                String folderName = paths[1];
                registerRollbackCleanup(zipFilePath, folderName);

                projectMapper.updateProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, findProjectUpload);
                projectUploadJpaRepository.save(findProjectUpload);

                boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_SOURCE_2 + zipDirectoryName);
                boolean isDirDeleted = FilePath.deleteDirectory(BASE_DIR_SOURCE_2 + directoryName);

                if (isFileDeleted && isDirDeleted) {
                    log.info("기존 파일과 디렉토리가 성공적으로 삭제되었습니다.");
                } else {
                    log.error("기존 파일 또는 디렉토리 삭제에 실패했습니다.");
                }
            }
        }

        log.info("프로젝트 수정 성공 - 사용자: {} 프로젝트 ID: {}", user.getName(), savedProject.getId());
        return projectMapper.projectToProjectResponse(savedProject);
    }

    /**
     * 프로젝트 삭제
     *
     * @param user       사용자 정보
     * @param projectIdx 프로젝트 인덱스
     * @return 삭제된 프로젝트 정보
     */
    @Override
    public ProjectResponse
    deleteProject(User user, Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(!project.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.error("프로젝트 삭제 권한이 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_DELETE_NOT_AUTHORIZED);
        }
        project.setDeletedAt();
        project.setState(INACTIVE);
        projectJpaRepository.save(project);
        List<Field> fields = project.getProjectFields().stream()
                .map(ProjectField::getField)
                .toList();
        if(project.getRepoName() == null) {
            statisticsService.adjustCount(project.getUser(), fields, project.getSemester(), project.getCategory(), 1, false);
        }
        else {
            statisticsService.adjustCount(project.getUser(), fields, project.getSemester(), project.getCategory(), 2, false);
        }
        log.info("프로젝트 삭제 성공 - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
        return projectMapper.projectToProjectResponse(project);


    }
    /**
     * 파일 저장 및 압축 해제
     *
     * @param file 저장할 파일
     * @return 압축 해제된 폴더명
     */
    private String[] storeAndUnzipFile(MultipartFile file) {
        log.info("파일 저장 및 압축 해제");
        String zipFilePath = FilePath.storeFile(file, PROJECT_ZIP);
        String folderName = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1, zipFilePath.toLowerCase().lastIndexOf(ZIP));
        UnZip.unzipFile(BASE_DIR_SOURCE + zipFilePath, BASE_DIR_SOURCE + PROJECT + '/' + folderName);
        return new String[] { zipFilePath, folderName };
    }

    /**
     * 트랜잭션 롤백 시 파일 삭제 로직 등록
     *
     * @param zipFilePath 압축 파일 경로
     * @param folderName  압축 해제된 폴더명
     */
    private void registerRollbackCleanup(String zipFilePath, String folderName) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    log.info("트랜잭션 롤백 시 파일 삭제 로직 실행");
                    log.info(BASE_DIR_SOURCE_2 + zipFilePath);
                    log.info(BASE_DIR_SOURCE_2 + PROJECT_UPLOAD + folderName);

                    boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_SOURCE_2 + zipFilePath);
                    boolean isDirDeleted = FilePath.deleteDirectory(BASE_DIR_SOURCE_2 + PROJECT_UPLOAD + folderName);

                    if (isFileDeleted && isDirDeleted) {
                        log.info("파일과 디렉토리가 성공적으로 삭제되었습니다.");
                    } else {
                        log.error("파일 또는 디렉토리 삭제에 실패했습니다.");
                    }
                }
            }
        });
    }

    /**
     * 프로젝트 필드 생성 및 저장
     *
     * @param fieldIdxList 필드 인덱스 리스트
     * @param project      프로젝트 엔티티
     * @return 생성된 ProjectField 리스트
     */
    private List<ProjectField>  createAndSaveProjectFields(List<Integer> fieldIdxList, Project project) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return projectMapper.createProjectField(project, field);
                }).toList();
    }
}

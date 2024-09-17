package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProjectField;
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
import inha.git.utils.file.FilePath;
import inha.git.utils.file.UnZip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private final ProjectMapper projectMapper;
    private final StatisticsService statisticsService;

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
        String[] paths = storeAndUnzipFile(file);
        String zipFilePath = paths[0];
        String folderName = paths[1];
        registerRollbackCleanup(zipFilePath, folderName);

        Semester semester = semesterJpaRepository.findByIdAndState(createProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Project project = projectMapper.createProjectRequestToProject(createProjectRequest, user, semester);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        ProjectUpload projectUpload = projectMapper.createProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, savedProject);
        projectUploadJpaRepository.save(projectUpload);

        List<ProjectField> projectFields = createAndSaveProjectFields(createProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        List<Field> fields = fieldJpaRepository.findAllById(createProjectRequest.fieldIdxList());

        statisticsService.increaseCount(user, fields, semester,  1);
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
        Semester semester = semesterJpaRepository.findByIdAndState(createGithubProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Project project = projectMapper.createGithubProjectRequestToProject(createGithubProjectRequest, user, semester);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        List<ProjectField> projectFields = createAndSaveProjectFields(createGithubProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);
        List<Field> fields = fieldJpaRepository.findAllById(createGithubProjectRequest.fieldIdxList());
        statisticsService.increaseCount(user, fields, semester,  8);
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
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(!project.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            log.error("프로젝트 수정 권한이 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), project.getId());
            throw new BaseException(PROJECT_NOT_AUTHORIZED);
        }
        Semester semester = semesterJpaRepository.findByIdAndState(updateProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));

        projectMapper.updateProjectRequestToProject(updateProjectRequest, project, semester);
        Project savedProject = projectJpaRepository.saveAndFlush(project);
        projectFieldJpaRepository.deleteByProject(savedProject);
        List<Field> originFields = savedProject.getProjectFields().stream()
                .map(ProjectField::getField)
                .toList();
        if(project.getRepoName() != null) {
            statisticsService.decreaseCount(user, originFields, semester,  8);
        }
        else {
            statisticsService.decreaseCount(user, originFields, semester,  1);
        }

        List<ProjectField> projectFields = createAndSaveProjectFields(updateProjectRequest.fieldIdxList(), savedProject);
        List<Field> fields = projectFieldJpaRepository.saveAll(projectFields).stream()
                .map(ProjectField::getField) // ProjectField에서 Field 객체만 추출
                .toList();

        if(project.getRepoName() == null) {
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
        if(project.getRepoName() != null) {
            statisticsService.increaseCount(user, fields, semester,  8);
        }
        else {
            statisticsService.increaseCount(user, fields, semester,  1);
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
            statisticsService.decreaseCount(project.getUser(), fields, project.getSemester(), 1);
        }
        else {
            statisticsService.decreaseCount(project.getUser(), fields, project.getSemester(), 8);
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
        String folderName = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1, zipFilePath.lastIndexOf(".zip"));
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
    private List<ProjectField> createAndSaveProjectFields(List<Integer> fieldIdxList, Project project) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return projectMapper.createProjectField(project, field);
                }).toList();
    }
}

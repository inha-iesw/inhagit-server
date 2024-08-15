package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.api.controller.api.response.UpdateProjectResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.user.domain.User;
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
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.PROJECT_NOT_FOUND;

/**
 * ProjectService는 프로젝트 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectUploadJpaRepository projectUploadJpaRepository;
    private final ProjectFieldJpaRepository projectFieldJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final ProjectMapper projectMapper;

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
    public CreateProjectResponse createProject(User user, CreateProjectRequest createProjectRequest, MultipartFile file) {
        String[] paths = storeAndUnzipFile(file);
        String zipFilePath = paths[0];
        String folderName = paths[1];

        registerRollbackCleanup(zipFilePath, folderName);

        Project project = projectMapper.createProjectRequestToProject(createProjectRequest, user);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        ProjectUpload projectUpload = projectMapper.createProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, savedProject);
        projectUploadJpaRepository.save(projectUpload);

        List<ProjectField> projectFields = createAndSaveProjectFields(createProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        return projectMapper.projectToCreateProjectResponse(savedProject);
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
    public UpdateProjectResponse updateProject(User user, Integer projectIdx, UpdateProjectRequest updateProjectRequest, MultipartFile file) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        ProjectUpload findProjectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        String directoryName = findProjectUpload.getDirectoryName();
        String zipDirectoryName = findProjectUpload.getZipDirectoryName();

        projectMapper.updateProjectRequestToProject(updateProjectRequest, project);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        projectFieldJpaRepository.deleteByProject(savedProject);

        List<ProjectField> projectFields = createAndSaveProjectFields(updateProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        if (file != null) {
            String[] paths = storeAndUnzipFile(file);
            String zipFilePath = paths[0];
            String folderName = paths[1];

            registerRollbackCleanup(zipFilePath, folderName);

            projectMapper.updateProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, findProjectUpload);
            projectUploadJpaRepository.save(findProjectUpload);

            boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_2 + zipDirectoryName);
            boolean isDirDeleted = FilePath.deleteDirectory(BASE_DIR_2 + directoryName);
            if (isFileDeleted && isDirDeleted) {
                log.info("기존 파일과 디렉토리가 성공적으로 삭제되었습니다.");
            } else {
                log.error("기존 파일 또는 디렉토리 삭제에 실패했습니다.");
            }
        }
        return projectMapper.projectToUpdateProjectResponse(savedProject);
    }

    /**
     * 파일 저장 및 압축 해제
     *
     * @param file 저장할 파일
     * @return 압축 해제된 폴더명
     */
    private String[] storeAndUnzipFile(MultipartFile file) {
        String zipFilePath = FilePath.storeFile(file, PROJECT_ZIP);
        String folderName = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1, zipFilePath.lastIndexOf(".zip"));
        UnZip.unzipFile(BASE_DIR + zipFilePath, folderName, PROJECT);
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
                    log.info(BASE_DIR_2 + zipFilePath);
                    log.info(BASE_DIR_2 + PROJECT_UPLOAD + folderName);

                    boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_2 + zipFilePath);
                    boolean isDirDeleted = FilePath.deleteDirectory(BASE_DIR_2 + PROJECT_UPLOAD + folderName);

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

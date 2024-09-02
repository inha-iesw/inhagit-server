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
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.file.FilePath;
import inha.git.utils.file.UnZip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final FieldJpaRepository fieldJpaRepository;
    private final ProjectPatentJpaRepository projectPatentJpaRepository;
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

        Project project = projectMapper.createProjectRequestToProject(createProjectRequest, user);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        ProjectUpload projectUpload = projectMapper.createProjectUpload(PROJECT_UPLOAD + folderName, zipFilePath, savedProject);
        projectUploadJpaRepository.save(projectUpload);

        List<ProjectField> projectFields = createAndSaveProjectFields(createProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        projectPatentJpaRepository.save(projectMapper.createProjectPatent(savedProject));
        statisticsService.increaseCount(user, 1);
        return projectMapper.projectToProjectResponse(savedProject);
    }

    /**
     * GitHub 프로젝트 클론 및 압축
     *
     * @param user                     사용자 정보
     * @param createGithubProjectRequest GitHub 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @Override
    @Transactional
    public ProjectResponse cloneAndZipProject(User user, CreateGithubProjectRequest createGithubProjectRequest) {
        String folderName = FilePath.generateFolderName();
        Path projectPath = FilePath.generateProjectPath(folderName);
        // GitHub 리포지토리 클론 수행
        try {
            Git.cloneRepository()
                    .setURI(GITHUB + createGithubProjectRequest.repoName() + GIT)
                    .setDirectory(projectPath.toFile())
                    .call();
        } catch (GitAPIException e) {
            throw new BaseException(GITHUB_CLONE_ERROR);
        }

        // 클론된 프로젝트 압축
        String zipFileName = folderName + ZIP;
        String zipRelativePath = PROJECT_ZIP + '/' + zipFileName;
        Path zipDestinationPath = Paths.get(BASE_DIR_2, zipRelativePath);

        FilePath.zipDirectory(projectPath, zipDestinationPath);

        // 트랜잭션 롤백 시 파일 삭제 로직 등록
        registerRollbackCleanup("/" + zipRelativePath, folderName);

        // 프로젝트 엔티티 생성 및 저장
        Project project = projectMapper.createGithubProjectRequestToProject(createGithubProjectRequest, user);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        ProjectUpload projectUpload = projectMapper.createProjectUpload(PROJECT_UPLOAD + folderName, "/" + zipRelativePath, savedProject);
        projectUploadJpaRepository.save(projectUpload);

        List<ProjectField> projectFields = createAndSaveProjectFields(createGithubProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);

        projectPatentJpaRepository.save(projectMapper.createProjectPatent(savedProject));
        statisticsService.increaseCount(user, 1);
        return projectMapper.projectToProjectResponse(savedProject);
    }

    //새로 깃클론 해오고 기존에 존재하던 거 디스크에서 삭제하고 디비 업데이트
    @Override
    @Transactional
    public ProjectResponse updateGithubProject(User user, Integer projectIdx) {
        // 기존 프로젝트 찾기
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if (!project.getUser().getId().equals(user.getId())) {
            throw new BaseException(PROJECT_NOT_AUTHORIZED);
        }
        // 기존 프로젝트 업로드 정보 찾기
        ProjectUpload findProjectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        // 새로 클론할 폴더 이름 및 경로 생성
        String folderName = FilePath.generateFolderName();
        Path projectPath = FilePath.generateProjectPath(folderName);

        // GitHub 리포지토리 클론 수행
        try {
            Git.cloneRepository()
                    .setURI(GITHUB + project.getRepoName() + GIT)
                    .setDirectory(projectPath.toFile())
                    .call();
        } catch (GitAPIException e) {
            throw new BaseException(GITHUB_CLONE_ERROR);
        }
        // 새로 클론한 프로젝트 압축
        String zipFileName = folderName + ZIP;
        String zipRelativePath = PROJECT_ZIP + '/' + zipFileName;
        Path zipDestinationPath = Paths.get(BASE_DIR_2, zipRelativePath);
        FilePath.zipDirectory(projectPath, zipDestinationPath);

        // 트랜잭션 롤백 시 파일 삭제 로직 등록
        registerRollbackCleanup("/" + zipRelativePath, folderName);

        // 기존 파일 및 디렉토리 삭제
        String oldDirectoryName = findProjectUpload.getDirectoryName();
        String oldZipDirectoryName = findProjectUpload.getZipDirectoryName();
        boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_2 + oldZipDirectoryName);
        boolean isDirDeleted = FilePath.deleteDirectory(BASE_DIR_2 + oldDirectoryName);
        if (isFileDeleted && isDirDeleted) {
            log.info("기존 파일과 디렉토리가 성공적으로 삭제되었습니다.");
        } else {
            log.error("기존 파일 또는 디렉토리 삭제에 실패했습니다.");
        }

        // 프로젝트 업로드 정보 업데이트
        projectMapper.updateProjectUpload(PROJECT_UPLOAD + folderName, "/" + zipRelativePath, findProjectUpload);
        projectUploadJpaRepository.save(findProjectUpload);

        return projectMapper.projectToProjectResponse(project);
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
            throw new BaseException(PROJECT_NOT_AUTHORIZED);
        }
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
            throw new BaseException(PROJECT_DELETE_NOT_AUTHORIZED);
        }
        project.setDeletedAt();
        project.setState(INACTIVE);
        projectJpaRepository.save(project);

        statisticsService.decreaseCount(user, 1);
        return projectMapper.projectToProjectResponse(project);


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

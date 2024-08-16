package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.PatentRecommendJpaRepository;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
import inha.git.mapping.domain.repository.RegistrationRecommendJpaRepository;
import inha.git.project.api.controller.api.response.*;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectQueryRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ProjectSearchService는 프로젝트 검색 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectSearchServiceImpl implements ProjectSearchService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectUploadJpaRepository projectUploadJpaRepository;
    private final ProjectFieldJpaRepository projectFieldJpaRepository;
    private final ProjectMapper projectMapper;
    private final ProjectQueryRepository projectQueryRepository;
    private final PatentRecommendJpaRepository patentRecommendJpaRepository;
    private final FoundingRecommendJpaRepository foundingRecommendJpaRepository;
    private final RegistrationRecommendJpaRepository registrationRecommendJpaRepository;

    /**
     * 프로젝트 전체 조회
     *
     * @param page 페이지 번호
     * @return 검색된 프로젝트 정보 페이지
     */
    @Override
    public Page<SearchProjectsResponse> getProjects(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return projectQueryRepository.getProjects(pageable);
    }

    /**
     * 프로젝트 상세 조회
     *
     * @param user      사용자 정보
     * @param projectIdx 프로젝트 번호
     * @return 프로젝트 상세 정보
     */
    @Override
    public SearchProjectResponse getProject(User user, Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        ProjectUpload projectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        List<SearchFieldResponse> searchFieldResponses = projectFieldJpaRepository.findByProject(project)
                .stream()
                .map(projectField -> projectMapper.projectFieldToSearchFieldResponse(projectField.getField()))
                .toList();

        SearchRecommendCount searchRecommendCountResponse = projectMapper.projectToSearchRecommendCountResponse(project);
        SearchUserResponse searchUserResponse = projectMapper.userToSearchUserResponse(project.getUser());

        boolean isRecommendPatent = patentRecommendJpaRepository.existsByUserAndProject(user, project);
        boolean isRecommendRegistration = registrationRecommendJpaRepository.existsByUserAndProject(user, project);
        boolean isRecommendFounding = foundingRecommendJpaRepository.existsByUserAndProject(user, project);

        SearchRecommendState searchRecommendState = projectMapper.projectToSearchRecommendState
                (isRecommendPatent, isRecommendFounding, isRecommendRegistration);

        return projectMapper.projectToSearchProjectResponse(
                project, projectUpload, searchFieldResponses, searchRecommendCountResponse, searchUserResponse, searchRecommendState
        );
    }

    /**
     * 프로젝트 파일 조회
     *
     * @param projectIdx 프로젝트 번호
     * @param path       파일 경로
     * @return 프로젝트 파일 정보
     */
    @Override
    public List<SearchFileResponse> getProjectFileByIdx(Integer projectIdx, String path) {
        ProjectUpload projectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        String absoluteFilePath = BASE_DIR + projectUpload.getDirectoryName() + path;
        Path filePath = Paths.get(absoluteFilePath);
        if (!Files.exists(filePath)) {
            throw new BaseException(FILE_NOT_FOUND);
        }
        try {
            if (Files.isDirectory(filePath)) {
                try (Stream<Path> paths = Files.list(filePath)) {
                    return paths
                            .filter(f -> !f.getFileName().toString().equals(GIT) && !f.getFileName().toString().equals(DS_STORE))
                            .map(this::mapToFileResponse)
                            .toList();
                }
            } else {
                return List.of(mapToFileResponse(filePath));
            }
        } catch (IOException e) {
            throw new BaseException(FILE_CONVERT);
        }
    }

    /**
     * 파일 정보를 SearchFileResponse로 변환
     *
     * @param path 파일 경로
     * @return 파일 정보
     */
    private SearchFileResponse mapToFileResponse(Path path) {
        return new SearchFileResponse(
                path.getFileName().toString(),
                Files.isDirectory(path) ? DIRECTORY : FILE
        );
    }
}
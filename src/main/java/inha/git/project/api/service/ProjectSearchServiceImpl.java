package inha.git.project.api.service;

import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.mapper.CategoryMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
import inha.git.mapping.domain.repository.ProjectLikeJpaRepository;
import inha.git.mapping.domain.repository.RegistrationRecommendJpaRepository;
import inha.git.project.api.controller.dto.request.SearchProjectCond;
import inha.git.project.api.controller.dto.response.*;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectQueryRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.user.domain.enums.Role.ADMIN;

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
    private final SemesterMapper semesterMapper;
    private final CategoryMapper categoryMapper;
    private final ProjectQueryRepository projectQueryRepository;
    private final ProjectLikeJpaRepository projectLikeJpaRepository;
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
     * 프로젝트 조건 조회
     *
     * @param searchProjectCond 검색 조건
     * @param page              페이지 번호
     * @return 검색된 프로젝트 정보 페이지
     */
    @Override
    public Page<SearchProjectsResponse> getCondProjects(SearchProjectCond searchProjectCond, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return projectQueryRepository.getCondProjects(searchProjectCond, pageable);
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
        Project project = findProject(projectIdx);

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }


        ProjectUpload projectUpload = getProjectUploadIfNeeded(project, projectIdx);
        SearchSemesterResponse searchSemesterResponse = semesterMapper.semesterToSearchSemesterResponse(project.getSemester());
        SearchCategoryResponse searchCategoryResponse = categoryMapper.categoryToCategoryResponse(project.getCategory());
        List<SearchFieldResponse> searchFieldResponses = projectFieldJpaRepository.findByProject(project)
                .stream()
                .map(projectField -> projectMapper.projectFieldToSearchFieldResponse(projectField.getField()))
                .toList();

        SearchRecommendCount searchRecommendCountResponse = projectMapper.projectToSearchRecommendCountResponse(project);
        SearchUserResponse searchUserResponse = projectMapper.userToSearchUserResponse(project.getUser());

        boolean isLike = projectLikeJpaRepository.existsByUserAndProject(user, project);
        boolean isRecommendRegistration = registrationRecommendJpaRepository.existsByUserAndProject(user, project);
        boolean isRecommendFounding = foundingRecommendJpaRepository.existsByUserAndProject(user, project);

        SearchRecommendState searchRecommendState = projectMapper.projectToSearchRecommendState
                (isLike, isRecommendFounding, isRecommendRegistration);

        return projectMapper.projectToSearchProjectResponse(
                project, projectUpload, searchFieldResponses, searchRecommendCountResponse, searchUserResponse, searchRecommendState, searchSemesterResponse, searchCategoryResponse
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
    public List<SearchFileResponse> getProjectFileByIdx(User user, Integer projectIdx, String path) {


        if (path.contains("..") || path.contains("\0")) {
            throw new BaseException(INVALID_FILE_PATH);
        }

        Project project = findProject(projectIdx);

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }


        ProjectUpload projectUpload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        String absoluteFilePath = BASE_DIR_SOURCE + projectUpload.getDirectoryName() + '/' + path;
        Path filePath = Paths.get(absoluteFilePath);
        if (!Files.exists(filePath)) {
            throw new BaseException(FILE_NOT_FOUND);
        }
        try {
            if (Files.isDirectory(filePath)) {
                try (Stream<Path> paths = Files.list(filePath)) {
                    return paths
                            .filter(f -> Files.isDirectory(f) ||
                                    f.getFileName().toString().contains(".") ||  // 점(.)이 있는 파일 필터링
                                    isSpecialFile(f))  // 확장자가 없더라도 중요한 파일 포함
                            .filter(f -> !f.getFileName().toString().equals(GIT) &&
                                    !f.getFileName().toString().equals(DS_STORE) &&
                                    !f.getFileName().toString().startsWith(UNDERBAR) &&
                                    !f.getFileName().toString().startsWith(MACOSX) &&
                                    !f.getFileName().toString().equals(PYCACHE) &&
                                    !f.getFileName().toString().contains(NODE_MODULES) &&
                                    !f.getFileName().toString().equals(IDEA) &&
                                    !f.getFileName().toString().endsWith(PYC) &&
                                    !f.getFileName().toString().endsWith(IML) &&
                                    !f.getFileName().toString().endsWith(OUT) &&
                                    !f.getFileName().toString().endsWith(DSYM) &&
                                    !f.getFileName().toString().endsWith(GRADLE) &&
                                    !f.getFileName().toString().endsWith(OUT_) &&
                                    !f.getFileName().toString().endsWith(BUILD) &&
                                    !f.getFileName().toString().endsWith(CLASS)
                            )
                            .map(this::mapToFileResponse)
                            .toList();
                }
            } else {
                String content = extractFileContent(filePath);
                return List.of(new SearchFileDetailResponse(filePath.getFileName().toString(), FILE, content));
            }
        } catch (IOException e) {
            log.error("Error reading file: " + e.getMessage(), e);
            throw new BaseException(FILE_CONVERT);
        }
    }

    /**
     * 프로젝트 찾아오는 함수
     *
     * @param projectIdx 프로젝트 번호
     * @return Project 프로젝트
     */
    private Project findProject(Integer projectIdx) {
        return projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
    }


    /**
     * 파일 정보를 SearchFileResponse로 변환
     *
     * @param path 파일 경로
     * @return 파일 정보
     */
    private SearchFileResponse mapToFileResponse(Path path) {
        if (Files.isDirectory(path)) {
            return new SearchDirectoryResponse(
                    path.getFileName().toString(),
                    DIRECTORY,
                    null // 하위 파일 리스트는 상위 메소드에서 처리됨
            );
        } else {
            return new SearchFileDetailResponse(
                    path.getFileName().toString(),
                    FILE,
                    null // 내용은 상위 메소드에서 처리됨
            );
        }
    }

    private String extractFileContent(Path filePath) throws IOException {
        String fileName = filePath.getFileName().toString().toLowerCase();
        String contentType = Files.probeContentType(filePath);

        // 파일 확장자에 따른 처리 추가
        if (fileName.endsWith(".sh") || fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            return Files.readString(filePath);
        }

        // MIME 타입에 따른 처리
        if (contentType != null) {
            if (contentType.equals("text/csv")) {
                // UTF-8 시도 후 실패하면 CP-949로 시도
                try {
                    return Files.readString(filePath);  // 기본적으로 UTF-8로 시도
                } catch (MalformedInputException e) {
                    log.info("UTF-8로 읽기 실패, MS949로 다시 시도합니다.");
                    try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.forName("MS949"))) {
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        return content.toString();
                    }
                }
            } else if (contentType.startsWith("image")) {
                // 이미지 파일 처리
                byte[] imageBytes = Files.readAllBytes(filePath);
                return Base64.getEncoder().encodeToString(imageBytes);
            } else if (contentType.startsWith("text") ||
                    contentType.contains("json") ||
                    contentType.contains("javascript") ||
                    contentType.contains("xml") ||
                    contentType.contains("yaml")) {
                // 텍스트, JSON, JavaScript, XML, YAML 파일 처리
                return Files.readString(filePath);
            }
        }

        // MIME 타입을 확인할 수 없을 때 기본적으로 텍스트 파일로 처리
        if (contentType == null || contentType.startsWith("text")) {
            return Files.readString(filePath);
        }

        return null;
    }


    /**
     * 프로젝트 업로드 정보 조회
     *
     * @param project 프로젝트
     * @param projectIdx 프로젝트 번호
     * @return 프로젝트 업로드 정보
     */
    private ProjectUpload getProjectUploadIfNeeded(Project project, Integer projectIdx) {
        if (project.getRepoName() == null) {
            return projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                    .orElseThrow(() -> new BaseException(PROJECT_UPLOAD_NOT_FOUND));
        }
        return null;
    }

    private boolean isSpecialFile(Path file) {
        String fileName = file.getFileName().toString();
        return fileName.equals("Dockerfile") ||
                fileName.equals("Makefile") ||
                fileName.equals("README") ||
                fileName.equals("LICENSE") ||
                fileName.equals("CHANGELOG") ||
                fileName.equals("VERSION") ||
                fileName.equals("Gemfile") ||
                fileName.equals("Rakefile") ||
                fileName.equals("Procfile") ||
                fileName.equals("Vagrantfile");
    }
}
package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.FoundingRecommendJpaRepository;
import inha.git.mapping.domain.repository.ProjectLikeJpaRepository;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
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
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        ProjectUpload projectUpload = getProjectUploadIfNeeded(project, projectIdx);

        log.info("semesterIdx {}", project.getSemester().getId());
        log.info("semesterName {}", project.getSemester().getName());
        SearchSemesterResponse searchSemesterResponse = semesterMapper.semesterToSearchSemesterResponse(project.getSemester());
        log.info("semesterResponse {}", searchSemesterResponse.idx());
        log.info("semesterResponse {}", searchSemesterResponse.name());
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
                project, projectUpload, searchFieldResponses, searchRecommendCountResponse, searchUserResponse, searchRecommendState, searchSemesterResponse
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
        log.info("path: {}", path);
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
                    List<SearchFileResponse> fileList = paths
                            .filter(f -> !f.getFileName().toString().equals(GIT) &&
                                    !f.getFileName().toString().equals(DS_STORE) &&
                                    !f.getFileName().toString().startsWith(UNDERBAR) &&
                                    !f.getFileName().toString().startsWith(MACOSX) &&
                                    !f.getFileName().toString().equals(PYCACHE) &&
                                    !f.getFileName().toString().endsWith(PYC))
                            .map(this::mapToFileResponse)
                            .toList();
                    return fileList;
                }
            } else {
                String content = extractFileContent(filePath);
                return List.of(new SearchFileDetailResponse(filePath.getFileName().toString(), "file", content));
            }
        } catch (IOException e) {
            log.error("Error reading file: " + e.getMessage(), e);
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
        if (Files.isDirectory(path)) {
            return new SearchDirectoryResponse(
                    path.getFileName().toString(),
                    "directory",
                    null // 하위 파일 리스트는 상위 메소드에서 처리됨
            );
        } else {
            return new SearchFileDetailResponse(
                    path.getFileName().toString(),
                    "file",
                    null // 내용은 상위 메소드에서 처리됨
            );
        }
    }

    private String extractFileContent(Path filePath) throws IOException {
        String contentType = Files.probeContentType(filePath);

        // 파일 MIME 타입에 따른 처리
        if (contentType != null && contentType.equals("text/csv")) {
            log.info("CSV 파일 처리 중: {}", filePath);

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
        } else if (contentType != null && contentType.startsWith("image")) {
            // 이미지 파일 처리
            byte[] imageBytes = Files.readAllBytes(filePath);
            return Base64.getEncoder().encodeToString(imageBytes);
        } else if (contentType != null && (contentType.startsWith("text") || contentType.contains("json") || contentType.contains("javascript"))) {
            // 텍스트 또는 JSON, JavaScript 파일 처리
            return Files.readString(filePath);
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
}
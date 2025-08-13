package inha.git.project.api.service.query;

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
import inha.git.project.domain.repository.ProjectTeamMemberJpaRepository;
import inha.git.project.domain.repository.ProjectUploadJpaRepository;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ProjectQueryServiceImpl는 프로젝트 조회 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectQueryServiceImpl implements ProjectQueryService {

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
    private final ProjectTeamMemberJpaRepository projectTeamMemberJpaRepository;

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
            ".sh", ".yml", ".yaml", ".sql", ".txt", ".json", ".xml",
            ".md", ".csv", ".js", ".html", ".css", ".java", ".py"
    );

    private static final Set<String> TEXT_MIME_PATTERNS = Set.of(
            "text/", "application/json", "application/xml", "application/javascript",
            "application/sql", "text/x-sql", "application/yaml"
    );

    /**
     * 프로젝트 조건 조회
     *
     * @param searchProjectCond 검색 조건
     * @param page              페이지 번호
     * @param size 페이지 사이즈
     * @return 검색된 프로젝트 정보 페이지
     */
    @Override
    public Page<SearchProjectsResponse> getCondProjects(SearchProjectCond searchProjectCond, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
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

        List<SearchPatentSummaryResponse> searchPatentSummaryResponse = projectMapper.projectToSearchPatentSummaryResponse(project);
        List<SearchTeamMemberResponse> searchTeamMemberResponses = projectTeamMemberJpaRepository.findByProject(project)
                .stream().map(projectTeamMember -> projectMapper.projectTeamMemberToSearchTeamMemberResponse(projectTeamMember)).toList();
        return projectMapper.projectToSearchProjectResponse(
                project, projectUpload, searchFieldResponses, searchRecommendCountResponse, searchUserResponse, searchRecommendState, searchSemesterResponse, searchCategoryResponse, searchPatentSummaryResponse, searchTeamMemberResponses
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
                            .map(p -> mapToFileResponse(p, projectIdx, path))
                            .toList();
                }
            } else {
                String content = extractFileContent(filePath);
                String fileUrl = buildFileUrl(projectIdx, path);
                return List.of(new SearchFileDetailResponse(
                        filePath.getFileName().toString(),
                        FILE,
                        content,
                        fileUrl
                ));
            }
        } catch (IOException e) {
            log.error("Error reading file: " + e.getMessage(), e);
            throw new BaseException(FILE_CONVERT);
        }
    }

    private Project findProject(Integer projectIdx) {
        return projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
    }

    private SearchFileResponse mapToFileResponse(Path child, Integer projectIdx, String currentPath) {
        String name = child.getFileName().toString();
        String base = (currentPath == null || currentPath.isBlank() || "/".equals(currentPath)) ? "" :
                (currentPath.endsWith("/") ? currentPath : currentPath + "/");
        String relPath = base + name;

        if (Files.isDirectory(child)) {
            return new SearchDirectoryResponse(
                    name,
                    DIRECTORY,
                    null
            );
        } else {
            // 파일이면 URL 세팅
            String fileUrl = buildFileUrl(projectIdx, relPath);
            return new SearchFileDetailResponse(
                    name,
                    FILE,
                    null,
                    fileUrl
            );
        }
    }

    public String extractFileContent(Path filePath) throws IOException {
        String fileName = filePath.getFileName().toString().toLowerCase();
        String extension = getFileExtension(fileName);
        String contentType = Files.probeContentType(filePath);

        if (TEXT_EXTENSIONS.contains(extension)) {
            return readTextFile(filePath);
        }
        // 2. MIME 타입으로 처리 방식 결정
        if (contentType != null) {
            // CSV 파일 특별 처리 (인코딩 문제)
            if (contentType.equals("text/csv") || fileName.endsWith(".csv")) {
                return readCsvFile(filePath);
            }
            // 이미지 파일 처리
            if (contentType.startsWith("image/")) {
                return readImageFile(filePath);
            }
            // 텍스트 계열 파일 처리
            if (isTextMimeType(contentType)) {
                return readTextFile(filePath);
            }
        }

        // 3. MIME 타입이 없거나 확인할 수 없는 경우 확장자나 휴리스틱 기반으로 처리
        if (contentType == null || isTextMimeType(contentType)) {
            return readTextFile(filePath);
        }
        // 처리할 수 없는 파일 타입
        log.warn("처리할 수 없는 파일 타입: {}, MIME: {}", fileName, contentType);
        return null;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    private boolean isTextMimeType(String mimeType) {
        if (mimeType == null) return false;
        return TEXT_MIME_PATTERNS.stream()
                .anyMatch(mimeType::contains);
    }

    private String readTextFile(Path filePath) throws IOException {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            log.warn("기본 방식으로 파일 읽기 실패: {}", e.getMessage());
            throw e;
        }
    }

    private String readCsvFile(Path filePath) throws IOException {
        try {
            return Files.readString(filePath); // 기본적으로 UTF-8로 시도
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
    }

    private String readImageFile(Path filePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

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

    private String buildFileUrl (Integer projectIdx, String relativePath){
        String enc = URLEncoder.encode(
                relativePath.startsWith("/") ? relativePath.substring(1) : relativePath,
                StandardCharsets.UTF_8);
        return "/api/v1/projects/" + projectIdx + "/file/download?path=" + enc;
    }

    public ResponseEntity<Resource> downloadProjectFile(User user, Integer projectIdx, String path) {
        if (path.contains("..") || path.contains("\0")) {
            throw new BaseException(INVALID_FILE_PATH);
        }

        Project project = findProject(projectIdx);
        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }

        ProjectUpload upload = projectUploadJpaRepository.findByProjectIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_UPLOAD_NOT_FOUND));

        Path absolute = Paths.get(BASE_DIR_SOURCE + upload.getDirectoryName() + '/' + path);
        if (!Files.exists(absolute) || Files.isDirectory(absolute)) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        try {
            String contentType = Files.probeContentType(absolute);
            if (contentType == null) contentType = "application/octet-stream";

            // 5) inline/attachment 분기
            boolean inline = contentType.startsWith("video/")
                    || contentType.startsWith("audio/")
                    || contentType.equals("application/pdf")
                    || contentType.startsWith("text/");

            String filename = absolute.getFileName().toString();
            String dispo = (inline ? "inline" : "attachment")
                    + "; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"";

            Resource body = new FileSystemResource(absolute);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, dispo)
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(Files.size(absolute))
                    .body(body);

        } catch (IOException e) {
            log.error("파일 스트리밍 실패: {}", e.getMessage(), e);
            throw new BaseException(FILE_CONVERT);
        }
    }
}

package inha.git.github.api.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inha.git.common.exceptions.BaseException;
import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.controller.dto.response.GithubFileContentResponse;
import inha.git.github.api.controller.dto.response.GithubItemResponse;
import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import inha.git.github.api.mapper.GithubMapper;
import inha.git.github.domain.repository.GithubTokenJpaRepository;
import inha.git.project.api.controller.dto.response.SearchDirectoryResponse;
import inha.git.project.api.controller.dto.response.SearchFileDetailResponse;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.RedisProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * Github 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GithubServiceImpl implements GithubService {
    private final UserJpaRepository userJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final GithubMapper githubMapper;
    private final GithubTokenJpaRepository githubTokenJpaRepository;
    private final RedisProvider redisProvider;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;



    /**
     * Github Token을 갱신합니다.
     *
     * @param user                사용자 정보
     * @param gitubTokenResquest Github Token 갱신 요청 정보
     * @return 갱신 성공 메시지
     */
    @Override
    @Transactional
    public String updateGithubToken(User user, GitubTokenResquest gitubTokenResquest) {
        if (!isValidGithubToken(gitubTokenResquest.githubToken())) {
            throw new BaseException(INVALID_GITHUB_TOKEN);
        }
        user.setGithubToken(gitubTokenResquest.githubToken());
        userJpaRepository.save(user);
        return "Github Token이 갱신되었습니다.";
    }

    /**
     * 사용자의 Github 레포지토리 목록을 조회합니다.
     *
     * @param user 사용자 정보
     * @return Github 레포지토리 목록
     */
    public List<GithubRepositoryResponse> getGithubRepositories(User user) {
        String githubToken = user.getGithubToken();
        if (githubToken == null) {
            log.error("Github Token이 없습니다. - 사용자: {}", user.getName());
            throw new BaseException(GITHUB_TOKEN_NOT_FOUND);
        }

        // Redis에서 캐시된 레포지토리 목록 조회
        String cacheKey = GITHUB_REPO_CACHE_PREFIX + user.getId(); // 유저별로 캐시 저장
        String cachedRepositories = redisProvider.getValueOps(cacheKey);

        // 캐시가 있으면 반환
        if (cachedRepositories != null) {
            log.info("Redis에서 Github 레포지토리 목록을 가져왔습니다. - 사용자: {}", user.getName());
            // 캐시된 JSON 데이터를 DTO로 변환하여 반환
            return fromJson(cachedRepositories);
        }

        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            List<GHRepository> repositories = github.getMyself().listRepositories().toList();
            String username = github.getMyself().getLogin(); // 현재 사용자의 GitHub 계정 가져오기

            // 내가 소유한 레포지토리만 필터링
            List<GithubRepositoryResponse> githubRepositoryResponses = repositories.stream()
                    .filter(repo -> !repo.isPrivate())  // 퍼블릭 레포지토리만 필터링
                    .filter(repo -> repo.getOwnerName().equals(username)) // 소유자가 현재 사용자와 같은지 확인
                    .sorted(Comparator.comparing(GHRepository::getId).reversed()) // 아이디 최신순으로 정렬
                    .map(githubMapper::toDto)
                    .toList();

            // 캐시가 없었으면, 데이터를 Redis에 캐싱
            log.info("Github 레포지토리 목록을 Redis에 저장합니다. - 사용자: {}", user.getName());
            redisProvider.setDataExpire(cacheKey, toJson(githubRepositoryResponses), 60); // 1분 동안 캐시 유지
            return githubRepositoryResponses;
        } catch (IOException e) {
            log.error("Github 레포지토리 목록을 가져오는데 실패했습니다. - 사용자: {} 에러메시지: {} ", user.getName(), e.getMessage());
            throw new BaseException(FAILED_TO_GET_GITHUB_REPOSITORIES);
        }
    }

    /**
     * Github Token이 유효한지 확인합니다.
     *
     * @param token Github Token
     * @return 유효 여부
     */
    private boolean isValidGithubToken(String token) {
        String url = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_AUTHORIZATION, TOKEN + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Github Token이 유효하지 않습니다. - 에러메시지: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 프로젝트의 Github 파일 목록을 조회합니다.
     *
     * @param user       사용자 정보
     * @param projectIdx 프로젝트 ID
     * @param path       파일 경로
     * @return 프로젝트 파일 목록
     */
    @Override
    public List<SearchFileResponse> getGithubFiles(User user, Integer projectIdx, String path) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        if (project.getRepoName() == null) {
            log.error("프로젝트의 Github 레포지토리가 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), projectIdx);
            throw new BaseException(GITHUB_REPO_NOT_FOUND);
        }

        if (!hasAccessToProject(project, user)) {
            throw new BaseException(PROJECT_NOT_PUBLIC);
        }

        // Redis 캐시 키 설정 (프로젝트와 경로에 따라 캐시 구분)
        String cacheKey = GITHUB_FILE_CACHE_PREFIX + project.getRepoName() + ":" + path;
        String cachedFiles = redisProvider.getValueOps(cacheKey);
        // 캐시가 있으면 반환
        if (cachedFiles != null) {
            log.info("Redis에서 Github 파일 목록을 가져왔습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), projectIdx);
            try {
                return objectMapper.readValue(cachedFiles, new TypeReference<>() {
                });
            } catch (Exception e) {
                log.error("Redis 캐시 데이터 변환 실패 - 사용자: {} 프로젝트 ID: {} 캐시 데이터: {} 에러: {}", user.getName(), projectIdx, cachedFiles, e.getMessage(), e);
            }
        }

        String fileCacheKey = GITHUB_FILE_CONTENT_CACHE_PREFIX + project.getRepoName() + ":" + path;
        String cachedFileContent = redisProvider.getValueOps(fileCacheKey);
        // 캐시가 있으면 반환
        if (cachedFileContent != null) {
            log.info("Redis에서 Github 파일 내용을 가져왔습니다. - 사용자: {} 레포지토리: {} 경로: {}", user.getName(), project.getRepoName(), path);
            return  List.of(fromJson(cachedFileContent, SearchFileDetailResponse.class));
        }
        // 캐시가 없거나 변환에 실패한 경우 GitHub API 호출
        log.info("Github 파일 목록을 가져옵니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), projectIdx);
        String url = GITHUB_API_URL + project.getRepoName() + GITHUB_CONTENTS + path;

        HttpHeaders headers = new HttpHeaders();
        String githubToken = project.getUser().getGithubToken();
        headers.set(HEADER_AUTHORIZATION, TOKEN + githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<GithubItemResponse>> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
        } catch (RestClientException e) {
            return List.of(getGithubFileContent(user, githubToken, project.getRepoName(), path, fileCacheKey));
        } catch (Exception e) {
            log.warn("사용자 토큰으로 GitHub API 호출에 실패했습니다. 관리자 토큰을 사용합니다. - 에러: {}", e.getMessage());
            String adminGithubToken = githubTokenJpaRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new BaseException(GITHUB_TOKEN_NOT_FOUND))
                    .getToken();
            headers.set(HEADER_AUTHORIZATION, TOKEN + adminGithubToken);
            entity = new HttpEntity<>(headers);

            try {
                response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<GithubItemResponse>>() {
                        }
                );
            }catch (Exception ex) {
                return List.of(getGithubFileContent(user, adminGithubToken, project.getRepoName(), path, fileCacheKey));
            }
        }

        List<GithubItemResponse> items = response.getBody();
        if (items == null) {
            log.error("Github 파일 목록이 없습니다. - 사용자: {} 프로젝트 ID: {}", user.getName(), projectIdx);
            throw new BaseException(FILE_NOT_FOUND);
        }

        List<SearchFileResponse> fileResponses = items.stream()
                .filter(f -> !f.name().equals(GIT) &&
                        !f.name().equals(DS_STORE) &&
                        !f.name().startsWith(UNDERBAR) &&
                        !f.name().startsWith(MACOSX) &&
                        !f.path().contains(NODE_MODULES) &&
                        !f.name().equals(PYC) &&
                        !f.name().equals(PYCACHE) &&
                        !f.name().equals(IDEA) &&
                        !f.name().endsWith(OUT) &&
                        !f.name().endsWith(IML) &&
                        !f.name().endsWith(DSYM) &&
                        !f.name().endsWith(GRADLE) &&
                        !f.name().endsWith(OUT_) &&
                        !f.name().endsWith(CLASS) &&
                        !f.name().endsWith(BUILD) &&
                        !f.name().endsWith(BAT))
                .map(this::mapToFileResponse)
                .toList();
        try {
            String jsonFileResponses = objectMapper.writerFor(new TypeReference<List<SearchFileResponse>>() {}).writeValueAsString(fileResponses);
            redisProvider.setDataExpire(cacheKey, jsonFileResponses, 3600);
        } catch (JsonProcessingException e) {
            log.error("Github 파일 목록을 JSON으로 변환하는데 실패했습니다. - 사용자: {} 프로젝트 ID: {} 에러: {}",
                    user.getName(), projectIdx, e.getMessage(), e);
        }
        return fileResponses;
    }

    /**
     * Github 파일을 SearchFileResponse로 매핑합니다.
     *
     * @param item Github 파일 정보
     * @return SearchFileResponse
     */
    private SearchFileResponse mapToFileResponse(GithubItemResponse item) {
        if (DIR.equals(item.type())) {
            return new SearchDirectoryResponse(item.name(), null);  // fileList는 null로 설정
        } else {
            return new SearchFileDetailResponse(item.name(), null);  // contents는 null로 설정
        }
    }

    /**
     * Github 파일 내용을 조회합니다.
     *
     * @param user         사용자 정보
     * @param githubToken  Github Token
     * @param repoName     레포지토리 이름
     * @param path         파일 경로
     * @param fileCacheKey 파일 캐시 키
     * @return 파일 내용
     */
    public SearchFileDetailResponse getGithubFileContent(User user, String githubToken, String repoName, String path, String fileCacheKey) {

        // 캐시가 없으면 GitHub API 호출
        String url = GITHUB_API_URL + repoName + GITHUB_CONTENTS + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_AUTHORIZATION, TOKEN_PREFIX + githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GithubFileContentResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubFileContentResponse.class);

        GithubFileContentResponse fileContent = response.getBody();
        if (fileContent == null || fileContent.content() == null) {
            log.error("Github 파일 내용을 가져오는데 실패했습니다. - 사용자: {} 경로: {}", user.getName(), path);
            throw new BaseException(FILE_NOT_FOUND);
        }

        String content;
        String fileName = fileContent.name();

        // 이미지 파일이나 텍스트 파일인지 구분하여 처리
        if (isBinaryFile(fileName)) {
            content = fileContent.content();
        } else {
            byte[] decodedBytes = Base64.getDecoder().decode(fileContent.content().replaceAll("\n", ""));
            content = new String(decodedBytes, StandardCharsets.UTF_8);
        }

        SearchFileDetailResponse fileDetailResponse = new SearchFileDetailResponse(fileName, FILE, content);

        // 파일 내용을 Redis에 캐싱 (TTL 1시간)
        redisProvider.setDataExpire(fileCacheKey, toJson(fileDetailResponse), 3600);

        return fileDetailResponse;
    }

    private boolean isBinaryFile(String fileName) {
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".gif") || fileName.endsWith(".svg") || fileName.endsWith(".ico") ||
                fileName.endsWith(".bmp") || fileName.endsWith(".webp");
    }

    private List<GithubRepositoryResponse> fromJson(String cachedRepositories) {
        try {
            GithubRepositoryResponse[] reposArray = objectMapper.readValue(cachedRepositories, GithubRepositoryResponse[].class);
            return Arrays.asList(reposArray);
        } catch (JsonProcessingException e) {
            throw new BaseException(JSON_CONVERT_ERROR);
        }
    }

    /**
     * GithubRepositoryResponse 리스트를 JSON으로 변환하는 메서드
     */
    private String toJson(List<GithubRepositoryResponse> repositories) {
        try {
            return objectMapper.writeValueAsString(repositories);
        } catch (JsonProcessingException e) {
            throw new BaseException(JSON_CONVERT_ERROR);
        }
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new BaseException(JSON_CONVERT_ERROR);
        }
    }
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환에 실패했습니다. - 에러메시지: {}", e.getMessage());
            throw new BaseException(JSON_CONVERT_ERROR);
        }
    }


}

package inha.git.github.api.service;


import inha.git.common.exceptions.BaseException;
import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.controller.dto.response.GithubFileContentDTO;
import inha.git.github.api.controller.dto.response.GithubItemDTO;
import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import inha.git.github.api.mapper.GithubMapper;
import inha.git.project.api.controller.dto.response.SearchDirectoryResponse;
import inha.git.project.api.controller.dto.response.SearchFileDetailResponse;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final RestTemplate restTemplate = new RestTemplate();

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
            throw new BaseException(GITHUB_TOKEN_NOT_FOUND);
        }
        try {
            GitHub github = GitHub.connectUsingOAuth(githubToken);
            List<GHRepository> repositories = github.getMyself().listRepositories().toList();

            String username = github.getMyself().getLogin(); // 현재 사용자의 GitHub 계정 가져오기

            // 내가 소유한 레포지토리만 필터링
            return repositories.stream()
                    .filter(repo -> repo.getOwnerName().equals(username)) // 소유자가 현재 사용자와 같은지 확인
                    .sorted(Comparator.comparing(GHRepository::getId).reversed()) // 아이디 최신순으로 정렬
                    .map(githubMapper::toDto)
                    .toList();
        } catch (IOException e) {
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
        headers.set("Authorization", "token " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
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
        if(project.getRepoName() == null) {
            throw new BaseException(GITHUB_REPO_NOT_FOUND);
        }
        log.info("path: {}", path);
        String url = "https://api.github.com/repos/" + project.getRepoName() + "/contents/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + user.getGithubToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<GithubItemDTO>> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<GithubItemDTO>>() {}
            );
        } catch (Exception e) {
            // If it's not a list, it might be a single file
            return List.of(getGithubFileContent(user, project.getRepoName(), path));
        }

        List<GithubItemDTO> items = response.getBody();

        if (items == null) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        return items.stream()
                .filter(f -> !f.getName().equals(GIT) &&
                        !f.getName().equals(DS_STORE) &&
                        !f.getName().startsWith(UNDERBAR) &&
                        !f.getName().startsWith(MACOSX))
                .map(item -> mapToFileResponse(item))
                .toList();
    }

    private SearchFileResponse mapToFileResponse(GithubItemDTO item) {
        if ("dir".equals(item.getType())) {
            return new SearchDirectoryResponse(item.getName(), "directory", null);
        } else {
            return new SearchFileDetailResponse(item.getName(), "file", null);
        }
    }

    public SearchFileDetailResponse getGithubFileContent(User user, String repoName, String path) {
        String url = "https://api.github.com/repos/" + repoName + "/contents/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getGithubToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GithubFileContentDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubFileContentDTO.class);

        GithubFileContentDTO fileContent = response.getBody();
        if (fileContent == null || fileContent.getContent() == null) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        String content;
        if ("base64".equals(fileContent.getEncoding())) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(fileContent.getContent().replaceAll("\n", ""));
                content = new String(decodedBytes, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                log.error("Base64 디코딩 중 오류 발생: " + e.getMessage());
                throw new BaseException(FILE_CONVERT);
            }
        } else {
            log.warn("파일이 Base64로 인코딩되지 않음. 인코딩 형식: " + fileContent.getEncoding());
            content = "파일이 Base64로 인코딩되지 않았습니다.";
        }
        return new SearchFileDetailResponse(fileContent.getName(), "file", content);
    }
}

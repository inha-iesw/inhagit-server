package inha.git.search.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.search.api.controller.dto.response.SearchResponse;
import inha.git.search.api.service.SearchService;
import inha.git.search.domain.enums.TableType;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.Constant.PROBLEM;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.SEARCH_OK;
import static inha.git.search.domain.enums.TableType.*;

/**
 * SearchController는 검색 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "search controller", description = "search 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/searches")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "게시글 검색 API", description = "게시글 검색을 수행합니다")
    public BaseResponse<Page<SearchResponse>> search(
            @AuthenticationPrincipal User user,
            @RequestParam("search") String search,
            @RequestParam("page") Integer page,
            @RequestParam(value = "type", required = false) TableType type) {

        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        if (search == null || search.trim().isEmpty()) {
            throw new BaseException(INVALID_SEARCH_QUERY); // 검색어가 null이거나 빈 문자열일 경우
        }
        if (search.startsWith(" ")) {
            throw new BaseException(INVALID_SEARCH_QUERY); // 검색어가 공백으로 시작하는 경우
        }

        if (!search.matches("^[a-zA-Z0-9가-힣 ]*$")) {
            throw new BaseException(INVALID_SEARCH_QUERY); // 특수문자가 포함된 경우
        }
        log.info("게시글 검색 - 사용자: {} 검색어: {}", user.getName(), search);
        return BaseResponse.of(SEARCH_OK, searchService.search(search, page - 1, type));
    }
}

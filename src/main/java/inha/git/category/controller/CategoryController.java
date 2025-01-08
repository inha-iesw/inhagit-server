package inha.git.category.controller;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.service.CategoryService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * 카테고리(교과/비교과/기타) 관련 API를 처리하는 컨트롤러입니다.
 * 카테고리 조회 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "category controller", description = "category 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 전체 카테고리 목록을 이름 기준으로 오름차순 조회합니다.
     *
     * @return 활성 상태인 모든 카테고리 정보를 포함하는 응답
     */
    @GetMapping
    @Operation(summary = "카테고리 전체 조회 API", description = "카테고리 전체를 조회합니다.")
    public BaseResponse<List<SearchCategoryResponse>> getCategories() {
        return BaseResponse.of(CATEGORY_SEARCH_OK, categoryService.getCategories());
    }


    /**
     * 새로운 카테고리를 생성합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param createCategoryRequest 생성할 카테고리 정보 (카테고리명)
     * @return 카테고리 생성 결과 메시지
     * @throws BaseException 관리자 권한이 없는 경우
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "카테고리 생성(관리자 전용) API", description = "카테고리를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createCategory(@AuthenticationPrincipal User user,
                                               @Validated @RequestBody CreateCategoryRequest createCategoryRequest) {
        log.info("카테고리 생성 - 관리자: {} 카테고리명: {}", user.getName(), createCategoryRequest.name());
        return BaseResponse.of(CATEGORY_CREATE_OK, categoryService.createCategory(user, createCategoryRequest));
    }

    /**
     * 기존 카테고리의 이름을 수정합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param categoryIdx 수정할 카테고리의 식별자
     * @param updateCategoryRequest 수정할 카테고리 정보 (새로운 카테고리명)
     * @return 카테고리 수정 결과 메시지
     * @throws BaseException CATEGORY_NOT_FOUND: 카테고리를 찾을 수 없는 경우
     */
    @PutMapping("/{categoryIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "카테고리 수정(관리자 전용) API", description = "카테고리를 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateCategory(@AuthenticationPrincipal User user,
                                               @PathVariable("categoryIdx") Integer categoryIdx,
                                               @Validated @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        log.info("카테고리 수정 - 관리자: {} 카테고리명: {}", user.getName(), updateCategoryRequest.name());
        return BaseResponse.of(CATEGORY_UPDATE_OK, categoryService.updateCategoryName(user, categoryIdx, updateCategoryRequest));
    }

    /**
     * 카테고리를 소프트 삭제(상태 변경) 처리합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param categoryIdx 삭제할 카테고리의 식별자
     * @return 카테고리 삭제 결과 메시지
     * @throws BaseException CATEGORY_NOT_FOUND: 카테고리를 찾을 수 없는 경우
     */
    @DeleteMapping("/{categoryIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "카테고리 삭제(관리자 전용) API", description = "카테고리를 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteCategory(@AuthenticationPrincipal User user,
                                               @PathVariable("categoryIdx") Integer categoryIdx) {
        log.info("카테고리 삭제 - 관리자: {} 카테고리명: {}", user.getName(), categoryIdx);
        return BaseResponse.of(CATEGORY_DELETE_OK, categoryService.deleteCategory(user, categoryIdx));
    }
}

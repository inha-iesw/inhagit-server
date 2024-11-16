package inha.git.category.controller;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.service.CategoryService;
import inha.git.common.BaseResponse;
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
 * CategoryController는 category 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "category controller", description = "category 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 전체 조회 API
     *
     * @return 카테고리 전체
     */
    @GetMapping
    @Operation(summary = "카테고리 전체 조회 API", description = "카테고리 전체를 조회합니다.")
    public BaseResponse<List<SearchCategoryResponse>> getCategories() {
        return BaseResponse.of(CATEGORY_SEARCH_OK, categoryService.getCategories());
    }


    /**
     * 카테고리 생성 API
     *
     * @param createCategoryRequest 카테고리 생성 요청
     * @return 생성된 카테고리 이름
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
     * 카테고리 수정 API
     *
     * @param categoryIdx 카테고리 인덱스
     * @param updateCategoryRequest 학기 수정 요청
     * @return 수정된 학기 이름
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
     * 카테고리 삭제 API
     *
     * @param categoryIdx 카테고리 인덱스
     * @return 삭제된 카테고리 이름
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

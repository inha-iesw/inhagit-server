package inha.git.category.controller;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.service.CategoryService;
import inha.git.common.BaseResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @Test
    @DisplayName("카테고리 전체 조회 성공")
    void getCategories_Success() {
        // given
        List<SearchCategoryResponse> expectedResponses = Arrays.asList(
                new SearchCategoryResponse(1, "교과"),
                new SearchCategoryResponse(2, "기타"),
                new SearchCategoryResponse(3, "비교과")
        );

        given(categoryService.getCategories())
                .willReturn(expectedResponses);

        // when
        BaseResponse<List<SearchCategoryResponse>> response =
                categoryController.getCategories();

        // then
        assertThat(response.getResult())
                .isEqualTo(expectedResponses);
        verify(categoryService).getCategories();
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_Success() {
        // given
        User admin = createAdminUser();
        CreateCategoryRequest request = new CreateCategoryRequest("신규카테고리");
        String expectedResponse = "신규카테고리 카테고리가 생성되었습니다.";

        given(categoryService.createCategory(admin, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response =
                categoryController.createCategory(admin, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(categoryService).createCategory(admin, request);
    }

    @Test
    @DisplayName("카테고리 이름 수정 성공")
    void updateCategory_Success() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 1;
        UpdateCategoryRequest request = new UpdateCategoryRequest("수정된카테고리");
        String expectedResponse = "수정된카테고리 카테고리 이름이 수정되었습니다.";

        given(categoryService.updateCategoryName(admin, categoryIdx, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response =
                categoryController.updateCategory(admin, categoryIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(categoryService).updateCategoryName(admin, categoryIdx, request);
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_Success() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 1;
        String expectedResponse = "테스트카테고리 카테고리 삭제되었습니다.";

        given(categoryService.deleteCategory(admin, categoryIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response =
                categoryController.deleteCategory(admin, categoryIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(categoryService).deleteCategory(admin, categoryIdx);
    }
    private User createAdminUser() {
        return User.builder()
                .id(1)
                .email("admin@test.com")
                .name("관리자")
                .role(Role.ADMIN)
                .build();
    }
}
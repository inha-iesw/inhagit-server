package inha.git.category.service;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.category.mapper.CategoryMapper;
import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.CATEGORY_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("카테고리 전체 조회 성공")
    void getCategories_Success() {
        // given
        List<Category> categories = Arrays.asList(
                createCategory(1, "교과"),
                createCategory(2, "기타"),
                createCategory(3, "비교과")
        );

        List<SearchCategoryResponse> expectedResponses = Arrays.asList(
                new SearchCategoryResponse(1, "교과"),
                new SearchCategoryResponse(2, "기타"),
                new SearchCategoryResponse(3, "비교과")
        );

        given(categoryJpaRepository.findAllByState(ACTIVE, Sort.by(Sort.Direction.ASC, "name")))
                .willReturn(categories);
        given(categoryMapper.categoriesToSearchCategoryResponses(categories))
                .willReturn(expectedResponses);

        // when
        List<SearchCategoryResponse> result = categoryService.getCategories();

        // then
        assertThat(result)
                .hasSize(3)
                .isEqualTo(expectedResponses);
        verify(categoryJpaRepository).findAllByState(ACTIVE, Sort.by(Sort.Direction.ASC, "name"));
    }

    private Category createCategory(int id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_Success() {
        // given
        User admin = createAdminUser();
        CreateCategoryRequest request = new CreateCategoryRequest("신규카테고리");
        Category category = Category.builder()
                .id(1)
                .name("신규카테고리")
                .build();

        given(categoryMapper.createCategoryRequestToSemester(request))
                .willReturn(category);
        given(categoryJpaRepository.save(any(Category.class)))
                .willReturn(category);

        // when
        String result = categoryService.createCategory(admin, request);

        // then
        assertThat(result).isEqualTo("신규카테고리 카테고리가 생성되었습니다.");
        verify(categoryJpaRepository).save(any(Category.class));
        verify(categoryMapper).createCategoryRequestToSemester(request);
    }

    @Test
    @DisplayName("중복된 카테고리명으로 생성 시도")
    void createCategory_DuplicateName_ThrowsException() {
        // given
        User admin = createAdminUser();
        CreateCategoryRequest request = new CreateCategoryRequest("기존카테고리");
        Category category = createCategory(request.name());

        given(categoryMapper.createCategoryRequestToSemester(request))
                .willReturn(category);
        given(categoryJpaRepository.save(any(Category.class)))
                .willThrow(new DataIntegrityViolationException("Duplicate entry"));

        // when & then
        assertThrows(DataIntegrityViolationException.class, () ->
                categoryService.createCategory(admin, request));
    }

    @Test
    @DisplayName("카테고리 이름 수정 성공")
    void updateCategoryName_Success() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 1;
        UpdateCategoryRequest request = new UpdateCategoryRequest("수정된카테고리");
        Category category = createCategory("기존카테고리");

        given(categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE))
                .willReturn(Optional.of(category));

        // when
        String result = categoryService.updateCategoryName(admin, categoryIdx, request);

        // then
        assertThat(result).isEqualTo("수정된카테고리 카테고리 이름이 수정되었습니다.");
        assertThat(category.getName()).isEqualTo("수정된카테고리");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 수정 시도")
    void updateCategoryName_CategoryNotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 999;
        UpdateCategoryRequest request = new UpdateCategoryRequest("수정된카테고리");

        given(categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.updateCategoryName(admin, categoryIdx, request));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_Success() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 1;
        Category category = createCategory("삭제할카테고리");

        given(categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE))
                .willReturn(Optional.of(category));

        // when
        String result = categoryService.deleteCategory(admin, categoryIdx);

        // then
        assertThat(result).isEqualTo("삭제할카테고리 카테고리 삭제되었습니다.");
        assertThat(category.getState()).isEqualTo(INACTIVE);
        assertThat(category.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제 시도")
    void deleteCategory_CategoryNotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer categoryIdx = 999;

        given(categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.deleteCategory(admin, categoryIdx));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(CATEGORY_NOT_FOUND.getMessage());
    }

    private Category createCategory(String name) {
        return Category.builder()
                .id(1)
                .name(name)
                .build();
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
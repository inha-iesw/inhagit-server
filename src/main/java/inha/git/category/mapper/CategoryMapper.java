package inha.git.category.mapper;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * CategoryMapper는 Category 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category createCategoryRequestToSemester(CreateCategoryRequest createCategoryRequest);

    @Mapping(source = "category.id", target = "idx")
    SearchCategoryResponse categoryToCategoryResponse(Category category);

    List<SearchCategoryResponse> categoriesToSearchCategoryResponses(List<Category> categoryList);
}

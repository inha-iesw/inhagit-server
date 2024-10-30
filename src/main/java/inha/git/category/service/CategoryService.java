package inha.git.category.service;

import inha.git.category.controller.dto.request.CreateCategoryRequest;
import inha.git.category.controller.dto.request.UpdateCategoryRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface CategoryService {

    List<SearchCategoryResponse> getCategories();
    String createCategory(User admin, CreateCategoryRequest createCategoryRequest);
    String updateCategoryName(User admin, Integer categoryIdx, UpdateCategoryRequest updateCategoryRequest);
    String deleteCategory(User admin, Integer categoryIdx);




}

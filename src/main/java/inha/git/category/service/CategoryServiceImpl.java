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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.CATEGORY_NOT_FOUND;

/**
 * 카테고리 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    /**
     * 모든 활성 상태 카테고리를 조회합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 활성 상태인 카테고리 조회<br>
     * 2. 이름 기준 오름차순 정렬<br>
     * 3. 응답 DTO로 변환<br>
     * </p>
     *
     * @return 카테고리 목록
     */
    @Override
    public List<SearchCategoryResponse> getCategories() {
        return categoryMapper.categoriesToSearchCategoryResponses
                (categoryJpaRepository.findAllByState(BaseEntity.State.ACTIVE, Sort.by(Sort.Direction.ASC, "name")));
    }

    /**
     * 새로운 카테고리를 생성하는 서비스입니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 카테고리 엔티티 생성<br>
     * 2. 데이터베이스에 저장<br>
     * </p>
     *
     * @param admin 카테고리를 생성하는 관리자 정보
     * @param createCategoryRequest 생성할 카테고리 정보
     * @return 카테고리 생성 완료 메시지
     */
    @Override
    @Transactional
    public String createCategory(User admin, CreateCategoryRequest createCategoryRequest) {
        Category category = categoryJpaRepository.save(categoryMapper.createCategoryRequestToSemester(createCategoryRequest));
        log.info("카테고리 생성 성공 - 관리자: {} 학기명: {}", admin.getName(), createCategoryRequest.name());
        return category.getName() + " 카테고리가 생성되었습니다.";
    }

    /**
     * 카테고리의 이름을 수정하는 서비스입니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 카테고리 존재 여부 확인<br>
     * 2. 카테고리 이름 업데이트<br>
     * </p>
     *
     * @param admin 수정을 요청한 관리자 정보
     * @param categoryIdx 수정할 카테고리의 식별자
     * @param updateCategoryRequest 새로운 카테고리 정보
     * @return 카테고리 수정 완료 메시지
     * @throws BaseException CATEGORY_NOT_FOUND: 카테고리를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String updateCategoryName(User admin, Integer categoryIdx, UpdateCategoryRequest updateCategoryRequest) {
        log.info("categoryIdx {}", categoryIdx);
        Category category = categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        category.setName(updateCategoryRequest.name());
        log.info("카테고리 이름 수정 성공 - 관리자: {} 카테고리명: {}", admin.getName(), updateCategoryRequest.name());
        return category.getName() + " 카테고리 이름이 수정되었습니다.";
    }

    /**
     * 카테고리를 삭제(비활성화) 처리하는 서비스입니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 카테고리 존재 여부 확인<br>
     * 2. 상태를 INACTIVE로 변경<br>
     * 3. 삭제 일시 기록<br>
     * </p>
     *
     * @param admin 삭제를 요청한 관리자 정보
     * @param categoryIdx 삭제할 카테고리의 식별자
     * @return 카테고리 삭제 완료 메시지
     * @throws BaseException CATEGORY_NOT_FOUND: 카테고리를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String deleteCategory(User admin, Integer categoryIdx) {
        Category category = categoryJpaRepository.findByIdAndState(categoryIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));
        category.setState(INACTIVE);
        category.setDeletedAt();
        log.info("카테고리 삭제 성공 - 관리자: {} 카테고리명: {}", admin.getName(), category.getName());
        return category.getName() + " 카테고리 삭제되었습니다.";
    }
}

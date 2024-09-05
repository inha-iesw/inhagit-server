package inha.git.search.api.service;

import inha.git.search.api.controller.dto.response.SearchResponse;
import inha.git.search.domain.repository.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.Constant.CREATE_AT;

/**
 * SearchServiceImpl은 SearchService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {

    private final SearchQueryRepository searchQueryRepository;

    /**
     * 검색
     *
     * @param search 검색어
     * @param page   페이지 정보
     * @return 검색 결과
     */
    @Override
    public Page<SearchResponse> search(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return searchQueryRepository.search(search, pageable);
    }
}

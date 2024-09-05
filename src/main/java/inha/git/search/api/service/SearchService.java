package inha.git.search.api.service;

import inha.git.search.api.controller.dto.response.SearchResponse;
import org.springframework.data.domain.Page;

public interface SearchService {

    Page<SearchResponse> search(String search, Integer page);
}

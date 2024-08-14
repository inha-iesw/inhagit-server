package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import org.springframework.data.domain.Page;

public interface AdminService {
    Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page);
}


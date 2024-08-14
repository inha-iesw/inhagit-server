package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import org.springframework.data.domain.Page;

public interface AdminService {

    Page<SearchUserResponse> getAdminUsers(String search, Integer page);
    Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page);


}


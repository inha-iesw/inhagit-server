package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import org.springframework.data.domain.Page;

public interface AdminSearchService {

    Page<SearchUserResponse> getAdminUsers(String search, Integer page);
    Page<SearchStudentResponse> getAdminStudents(String search, Integer page);
    Page<SearchProfessorResponse> getAdminProfessors(String search, Integer page);
    Page<SearchCompanyResponse> getAdminCompanies(String search, Integer page);
}

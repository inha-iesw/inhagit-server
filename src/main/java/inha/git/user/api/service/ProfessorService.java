package inha.git.user.api.service;

import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import org.springframework.data.domain.Page;

public interface ProfessorService {
    Page<SearchStudentResponse> getProfessorStudents(String search, Integer page);
    ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest);
}

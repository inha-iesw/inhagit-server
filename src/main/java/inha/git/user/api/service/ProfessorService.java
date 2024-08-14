package inha.git.user.api.service;

import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;

public interface ProfessorService {

    ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest);
}

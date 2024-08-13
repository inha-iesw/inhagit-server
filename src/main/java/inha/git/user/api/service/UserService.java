package inha.git.user.api.service;

import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;

public interface UserService {
    StudentSignupResponse studentSignup(StudentSignupRequest studentSignupRequest);

    ProfessorSignupResponse professorSignup(ProfessorSignupRequest professorSignupRequest);
}

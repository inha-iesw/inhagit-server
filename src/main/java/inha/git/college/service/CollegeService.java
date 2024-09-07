package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;

public interface CollegeService {

    String createCollege(CreateCollegeRequest createDepartmentRequest);
}

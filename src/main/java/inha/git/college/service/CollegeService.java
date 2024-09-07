package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;

public interface CollegeService {

    String createCollege(CreateCollegeRequest createDepartmentRequest);
    String updateCollegeName(Integer collegeIdx, UpdateCollegeRequest updateCollegeRequest);
}

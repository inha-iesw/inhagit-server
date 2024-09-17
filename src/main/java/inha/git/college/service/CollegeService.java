package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface CollegeService {

    List<SearchCollegeResponse> getColleges();
    SearchCollegeResponse getCollege(Integer departmentIdx);
    String createCollege(User admin, CreateCollegeRequest createDepartmentRequest);
    String updateCollegeName(User admin, Integer collegeIdx, UpdateCollegeRequest updateCollegeRequest);

    String deleteCollege(User admin, Integer collegeIdx);




}

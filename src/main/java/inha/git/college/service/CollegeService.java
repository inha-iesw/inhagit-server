package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;

import java.util.List;

public interface CollegeService {

    List<SearchCollegeResponse> getColleges();
    SearchCollegeResponse getCollege(Integer departmentIdx);
    String createCollege(CreateCollegeRequest createDepartmentRequest);
    String updateCollegeName(Integer collegeIdx, UpdateCollegeRequest updateCollegeRequest);

    String deleteCollege(Integer collegeIdx);




}

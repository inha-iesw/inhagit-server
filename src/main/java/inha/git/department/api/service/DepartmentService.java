package inha.git.department.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.user.domain.User;

import java.util.List;

public interface DepartmentService {
    List<SearchDepartmentResponse> getDepartments(Integer collegeIdx);
    String createDepartment(User admin, CreateDepartmentRequest createDepartmentRequest);
    String updateDepartmentName(User admin, Integer departmentIdx, UpdateDepartmentRequest updateDepartmentRequest);
    String deleteDepartment(User admin, Integer departmentIdx);
}

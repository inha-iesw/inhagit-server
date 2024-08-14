package inha.git.department.api.service;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;

import java.util.List;

public interface DepartmentService {

    List<SearchDepartmentResponse> getDepartments();
    String createDepartment(CreateDepartmentRequest createDepartmentRequest);

    String updateDepartmentName(Integer departmentIdx, UpdateDepartmentRequest updateDepartmentRequest);

    String deleteDepartment(Integer departmentIdx);
}


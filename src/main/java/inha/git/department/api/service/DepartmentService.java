package inha.git.department.api.service;

import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;

public interface DepartmentService {
    String createDepartment(CreateDepartmentRequest createDepartmentRequest);
}

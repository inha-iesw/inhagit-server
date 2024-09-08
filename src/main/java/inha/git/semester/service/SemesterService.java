package inha.git.semester.service;

import inha.git.semester.controller.dto.request.CreateSemesterRequest;

public interface SemesterService {

    String createSemester(CreateSemesterRequest createDepartmentRequest);
}

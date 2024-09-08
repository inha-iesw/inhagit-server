package inha.git.semester.service;

import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;

public interface SemesterService {

    String createSemester(CreateSemesterRequest createDepartmentRequest);
    String updateSemesterName(Integer semesterIdx, UpdateSemesterRequest updateSemesterRequest);

}

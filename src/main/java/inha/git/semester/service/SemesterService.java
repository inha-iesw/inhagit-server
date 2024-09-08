package inha.git.semester.service;

import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;

import java.util.List;

public interface SemesterService {

    List<SearchSemesterResponse> getSemesters();
    String createSemester(CreateSemesterRequest createDepartmentRequest);
    String updateSemesterName(Integer semesterIdx, UpdateSemesterRequest updateSemesterRequest);
    String deleteSemester(Integer semesterIdx);




}

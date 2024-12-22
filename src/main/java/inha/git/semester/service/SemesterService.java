package inha.git.semester.service;

import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface SemesterService {

    List<SearchSemesterResponse> getSemesters();
    String createSemester(User admin, CreateSemesterRequest createSemesterRequest);
    String updateSemesterName(User admin, Integer semesterIdx, UpdateSemesterRequest updateSemesterRequest);
    String deleteSemester(User admin, Integer semesterIdx);




}

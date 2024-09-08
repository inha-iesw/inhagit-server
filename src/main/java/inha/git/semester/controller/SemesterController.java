package inha.git.semester.controller;

import inha.git.common.BaseResponse;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.service.SemesterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * SemesterController는 semester 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "semester controller", description = "semester 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/semesters")
public class SemesterController {

    private final SemesterService semesterService;


}

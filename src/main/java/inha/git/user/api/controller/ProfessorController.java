package inha.git.user.api.controller;

import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.service.ProfessorService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.STUDENT_SEARCH_OK;

@Slf4j
@Tag(name = "professor controller", description = "교수 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/professors")
public class ProfessorController {

    private final ProfessorService professorService;

    /**
     * 교수 전용 학생 검색 API
     *
     * <p>교수 전용 학생 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 학생 정보를 포함하는 BaseResponse<Page<SearchStudentResponse>>
     */
    @GetMapping("/students")
    @Operation(summary = "학생 검색 API(교수/관리자 전용)", description = "교수 전용 학생 검색 API입니다")
    public BaseResponse<Page<SearchStudentResponse>> getProfessorStudents(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 교수 학생 검색 - 검색어: {}, 페이지: {}", user.getName(), search, page);
        return BaseResponse.of(STUDENT_SEARCH_OK, professorService.getProfessorStudents(search, page - 1));
    }
}

package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.service.QuestionService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.ErrorStatus.COMPANY_CANNOT_CREATE_QUESTION;
import static inha.git.common.code.status.SuccessStatus.QUESTION_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.QUESTION_UPDATE_OK;

/**
 * QuestionController는 question 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "question controller", description = "question 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 질문 생성(기업제외) API
     *
     * <p>질문을 생성합니다.</p>
     *
     * @param user                  User
     * @param createQuestionRequest CreateQuestionRequest
     * @return 생성된 질문 정보를 포함하는 BaseResponse<QuestionResponse>
     */
    @PostMapping
    @Operation(summary = "질문 생성(기업제외) API", description = "질문을 생성합니다.")
    public BaseResponse<QuestionResponse> createQuestion(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateQuestionRequest createQuestionRequest) {
        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_QUESTION);
        }
        return BaseResponse.of(QUESTION_CREATE_OK, questionService.createQuestion(user, createQuestionRequest));
    }

    /**
     * 질문 수정 API
     *
     * <p>질문을 수정합니다.</p>
     *
     * @param user                  User
     * @param questionIdx           Integer
     * @param updateQuestionRequest UpdateQuestionRequest
     * @return 수정된 질문 정보를 포함하는 BaseResponse<QuestionResponse>
     */
    @PutMapping("/{questionIdx}")
    @Operation(summary = "질문 수정 API", description = "질문을 수정합니다.")
    public BaseResponse<QuestionResponse> updateQuestion(
            @AuthenticationPrincipal User user,
            @PathVariable("questionIdx") Integer questionIdx,
            @Validated @RequestBody UpdateQuestionRequest updateQuestionRequest) {
        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_QUESTION);
        }
        return BaseResponse.of(QUESTION_UPDATE_OK, questionService.updateQuestion(user, questionIdx, updateQuestionRequest));
    }

}

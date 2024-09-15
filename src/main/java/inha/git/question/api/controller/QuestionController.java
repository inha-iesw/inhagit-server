package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.RecommendRequest;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.LikeRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.service.QuestionService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.ErrorStatus.COMPANY_CANNOT_CREATE_QUESTION;
import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.*;

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
     * 질문 전체 조회 API
     *
     * <p>질문 전체를 조회합니다.</p>
     *
     * @param page Integer
     * @return 검색된 질문 정보를 포함하는 BaseResponse<Page<SearchQuestionsResponse>>
     */
    @GetMapping
    @Operation(summary = "질문 전체 조회 API", description = "질문 전체를 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getQuestions(@RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(QUESTION_SEARCH_OK, questionService.getQuestions(page - 1));
    }

    /**
     * 질문 조건 조회 API
     *
     * <p>질문 조건에 맞게 조회합니다.</p>
     *
     * @param page Integer
     * @param searchQuestionCond SearchQuestionCond
     * @return 검색된 질문 정보를 포함하는 BaseResponse<Page<SearchQuestionsResponse>>
     */
    @GetMapping("/cond")
    @Operation(summary = "질문 조건 조회 API", description = "질문 조건에 맞게 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getCondQuestions(@RequestParam("page") Integer page, SearchQuestionCond searchQuestionCond) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(QUESTION_SEARCH_OK, questionService.getCondQuestions(searchQuestionCond, page - 1));
    }

    /**
     * 질문 상세 조회 API
     *
     * <p>질문 상세를 조회합니다.</p>
     *
     * @param questionIdx Integer
     * @return 검색된 질문 정보를 포함하는 BaseResponse<SearchQuestionResponse>
     */
    @GetMapping("/{questionIdx}")
    @Operation(summary = "질문 상세 조회 API", description = "질문 상세를 조회합니다.")
    public BaseResponse<SearchQuestionResponse> getQuestion(@PathVariable("questionIdx") Integer questionIdx) {
        return BaseResponse.of(QUESTION_DETAIL_OK, questionService.getQuestion(questionIdx));
    }
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
        return BaseResponse.of(QUESTION_DELETE_OK, questionService.updateQuestion(user, questionIdx, updateQuestionRequest));
    }

    /**
     * 질문 삭제 API
     *
     * <p>질문을 삭제합니다.</p>
     *
     * @param user        User
     * @param questionIdx Integer
     * @return 삭제된 질문 정보를 포함하는 BaseResponse<QuestionResponse>
     */
    @DeleteMapping("/{questionIdx}")
    @Operation(summary = "질문 삭제 API", description = "질문을 삭제합니다.")
    public BaseResponse<QuestionResponse> deleteQuestion(
            @AuthenticationPrincipal User user,
            @PathVariable("questionIdx") Integer questionIdx) {
        return BaseResponse.of(QUESTION_DELETE_OK, questionService.deleteQuestion(user, questionIdx));
    }

    /**
     * 질문 좋아요 API
     *
     * <p>특정 질문에 좋아요를 합니다.</p>
     *
     * @param user       로그인한 사용자 정보
     * @param likeRequest 좋아요할 질문 정보
     * @return 좋아요 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/like")
    @Operation(summary = "질문 좋아요 API", description = "특정 질문에 좋아요를 합니다.")
    public BaseResponse<String> questionLike(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid LikeRequest likeRequest) {
        return BaseResponse.of(LIKE_SUCCESS, questionService.createQuestionLike(user,likeRequest));
    }

}

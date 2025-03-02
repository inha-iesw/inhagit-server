package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.LikeRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.api.service.QuestionService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.PagingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * 질문 관련 API를 처리하는 컨트롤러입니다.
 * 질문의 조회, 생성, 수정, 삭제 및 좋아요 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "question controller", description = "question 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 전체 질문을 페이징하여 조회합니다.
     *
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이징된 질문 목록
     * @throws BaseException INVALID_PAGE: 페이지 번호가 유효하지 않은 경우
     *                      INVALID_SIZE: 페이지 크기가 유효하지 않은 경우
     */
    @GetMapping
    @Operation(summary = "질문 전체 조회 API", description = "질문 전체를 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getQuestions(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        PagingUtils.validatePage(page, size);
        return BaseResponse.of(QUESTION_SEARCH_OK, questionService.getQuestions(PagingUtils.toPageIndex(page), size));
    }

    /**
     * 질문 조건 조회 API
     *
     * @param page Integer
     * @param size Integer
     * @param searchQuestionCond SearchQuestionCond
     * @return 검색된 질문 정보를 포함하는 BaseResponse<Page<SearchQuestionsResponse>>
     */
    @GetMapping("/cond")
    @Operation(summary = "질문 조건 조회 API", description = "질문 조건에 맞게 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getCondQuestions(@RequestParam("page") Integer page, @RequestParam("size") Integer size , SearchQuestionCond searchQuestionCond) {
        PagingUtils.validatePage(page, size);
        return BaseResponse.of(QUESTION_SEARCH_OK, questionService.getCondQuestions(searchQuestionCond, PagingUtils.toPageIndex(page), size));
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
    public BaseResponse<SearchQuestionResponse> getQuestion(@AuthenticationPrincipal User user,
                                                            @PathVariable("questionIdx") Integer questionIdx) {
        return BaseResponse.of(QUESTION_DETAIL_OK, questionService.getQuestion(user, questionIdx));
    }

    /**
     * 질문 생성(기업제외) API
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
            log.error("기업은 프로젝트를 생성할 수 없습니다. - 사용자: {}", user.getName());
            throw new BaseException(COMPANY_CANNOT_CREATE_QUESTION);
        }
        log.info("질문 생성 - 사용자: {} 질문 제목: {}", user.getName(), createQuestionRequest.title());
        return BaseResponse.of(QUESTION_CREATE_OK, questionService.createQuestion(user, createQuestionRequest));
    }

    /**
     * 질문 수정 API
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
        log.info("질문 수정 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
        return BaseResponse.of(QUESTION_DELETE_OK, questionService.updateQuestion(user, questionIdx, updateQuestionRequest));
    }

    /**
     * 질문 삭제 API
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
        log.info("질문 좋아요 - 사용자: {} 질문 ID: {}", user.getName(), likeRequest.idx());
        return BaseResponse.of(LIKE_SUCCESS, questionService.createQuestionLike(user,likeRequest));
    }

    /**
     * 질문 좋아요 취소 API
     *
     * @param user       로그인한 사용자 정보
     * @param likeRequest 좋아요할 질문 정보
     * @return 좋아요 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/like")
    @Operation(summary = "질문 좋아요 취소 API", description = "특정 질문에 좋아요를 취소합니다.")
    public BaseResponse<String> questionLikeCancel(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid LikeRequest likeRequest) {
        log.info("질문 좋아요 취소 - 사용자: {} 질문 ID: {}", user.getName(), likeRequest.idx());
        return BaseResponse.of(LIKE_CANCEL_SUCCESS, questionService.questionLikeCancel(user,likeRequest));
    }
}

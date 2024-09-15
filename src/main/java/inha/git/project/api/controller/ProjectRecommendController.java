package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.dto.request.RecommendRequest;
import inha.git.project.api.service.ProjectRecommendService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * ProjectController는 project 추천 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project recommend controller", description = "project recommend 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects/recommend")
public class ProjectRecommendController {

    private final ProjectRecommendService projectRecommendService;

    /**
     * 프로젝트 창업 추천 API
     *
     * <p>특정 프로젝트에 창업 추천을 합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/founding")
    @Operation(summary = "프로젝트 창업 추천 API", description = "특정 프로젝트에 창업 추천을 합니다.")
    public BaseResponse<String> recommendFounding(@AuthenticationPrincipal User user,
                                                  @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(FOUNDING_RECOMMEND_SUCCESS, projectRecommendService.createProjectFoundingRecommend(user,recommendRequest));
    }

    /**
     * 프로젝트 좋아요 API
     *
     * <p>특정 프로젝트에 좋아요를 합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 좋아요할 프로젝트 정보
     * @return 좋아요 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/like")
    @Operation(summary = "프로젝트 좋아요 API", description = "특정 프로젝트에 좋아요를 합니다.")
    public BaseResponse<String> projectLike(@AuthenticationPrincipal User user,
                                                @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(LIKE_SUCCESS, projectRecommendService.createProjectLike(user,recommendRequest));
    }

    /**
     * 프로젝트 등록 추천 API
     *
     * <p>특정 프로젝트에 등록 추천을 합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 성공 메시지를 포함하는 BaseResponse<String>
     */
    @PostMapping("/registration")
    @Operation(summary = "프로젝트 등록 추천 API", description = "특정 프로젝트에 등록 추천을 합니다.")
    public BaseResponse<String> recommendRegistration(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(REGISTRATION_RECOMMEND_SUCCESS, projectRecommendService.createProjectRegistrationRecommend(user,recommendRequest));
    }

    /**
     * 프로젝트 창업 추천 취소 API
     *
     * <p>특정 프로젝트에 창업 추천을 취소합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/founding")
    @Operation(summary = "프로젝트 창업 추천 취소 API", description = "특정 프로젝트에 창업 추천을 취소합니다.")
    public BaseResponse<String> cancelFoundingRecommend(@AuthenticationPrincipal User user,
                                                        @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(FOUNDING_RECOMMEND_CANCEL_SUCCESS, projectRecommendService.cancelProjectFoundingRecommend(user,recommendRequest));
    }

    /**
     * 프로젝트 좋아요 취소 API
     *
     * <p>특정 프로젝트에 좋아요를 취소합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 좋아요 취소할 프로젝트 정보
     * @return 좋아요 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/like")
    @Operation(summary = "프로젝트 좋아요 취소 API", description = "특정 프로젝트에 좋아요를 취소합니다.")
    public BaseResponse<String> cancelPatentRecommend(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(PATENT_LIKE_CANCEL_SUCCESS, projectRecommendService.cancelProjectLike(user,recommendRequest));
    }

    /**
     * 프로젝트 등록 추천 취소 API
     *
     * <p>특정 프로젝트에 등록 추천을 취소합니다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param recommendRequest 추천할 프로젝트 정보
     * @return 추천 취소 성공 메시지를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/registration")
    @Operation(summary = "프로젝트 등록 추천 취소 API", description = "특정 프로젝트에 등록 추천을 취소합니다.")
    public BaseResponse<String> cancelRegistrationRecommend(@AuthenticationPrincipal User user,
                                                            @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(REGISTRATION_RECOMMEND_CANCEL_SUCCESS, projectRecommendService.cancelProjectRegistrationRecommend(user,recommendRequest));
    }

}

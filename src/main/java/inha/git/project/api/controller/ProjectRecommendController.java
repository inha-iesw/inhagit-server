package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.api.request.RecommendRequest;
import inha.git.project.api.service.ProjectRecommendService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * ProjectController는 project 추천 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project recommend controller", description = "project recommend 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
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
    @PostMapping("/recommend/founding")
    @Operation(summary = "프로젝트 창업 추천 API", description = "특정 프로젝트에 창업 추천을 합니다.")
    public BaseResponse<String> recommendFounding(@AuthenticationPrincipal User user,
                                                  @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(FOUNDING_RECOMMEND_SUCCESS, projectRecommendService.createProjectFoundingRecommend(user,recommendRequest));
    }

    @PostMapping("/recommend/patent")
    @Operation(summary = "프로젝트 특허 추천 API", description = "특정 프로젝트에 특허 추천을 합니다.")
    public BaseResponse<String> recommendPatent(@AuthenticationPrincipal User user,
                                                @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(PATENT_RECOMMEND_SUCCESS, projectRecommendService.createProjectPatentRecommend(user,recommendRequest));
    }

    @PostMapping("/recommend/registration")
    @Operation(summary = "프로젝트 등록 추천 API", description = "특정 프로젝트에 등록 추천을 합니다.")
    public BaseResponse<String> recommendRegistration(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid RecommendRequest recommendRequest) {
        return BaseResponse.of(REGISTRATION_RECOMMEND_SUCCESS, projectRecommendService.createProjectRegistrationRecommend(user,recommendRequest));
    }

}

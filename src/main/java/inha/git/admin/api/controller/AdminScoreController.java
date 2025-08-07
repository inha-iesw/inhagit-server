package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.UserRankingScoreRequest;
import inha.git.admin.api.service.AdminScoreService;
import inha.git.common.BaseResponse;
import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.api.service.UserService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.*;


/**
 * UserRanking adminScore 컬럼에 관리자 입력값을 넣는 컨트롤러입니다.
 * */
@Slf4j
@Tag(name = "Admin Score Controller", description = "관리자 권한 유저 점수 관리 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminScoreController {

    private final AdminScoreService adminScoreService;

    /**
     * @param user                      User
     * @param userRankingScoreRequest   UserRankingScoreRequest
     * */
    @PostMapping("/userScores")
    @Operation(summary = "유저 관리자 점수 업데이트 API", description = "관리자가 특정 유저의 점수를 직접 수정합니다.")
    public BaseResponse<UserRankingResponse> updateScore(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody UserRankingScoreRequest userRankingScoreRequest) {

        log.info("관리자 점수 업데이트 완료 - 대상 유저 인덱스: {}, 점수: {}", userRankingScoreRequest.userIdx(), userRankingScoreRequest.adminScore());
        return BaseResponse.of(ADMIN_USER_RANKING_SCORE_OK, adminScoreService.updateScore(userRankingScoreRequest));
    }

}

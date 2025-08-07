package inha.git.user.api.controller;

import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.api.service.UserRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 사용자 랭킹 관련 API를 처리하는 컨트롤러입니다.
 * 일반 사용자, 학생, 교수, 기업회원의 Ranking 점수를 관리합니다.
 */
@Slf4j
@Tag(name = "user Ranking Controller", description = "유저 랭킹 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/userRanking")
public class UserRankingController {

    private final UserRankingService userRankingService;

    /**
     * 상위 10명의 사용자 랭킹 정보를 반환합니다.
     */
    @GetMapping("/top10")
    public ResponseEntity<List<UserRankingResponse>> getTop10Rankings() {
        List<UserRankingResponse> rankings = userRankingService.getUserRankings();
        return ResponseEntity.ok(rankings);
    }
}

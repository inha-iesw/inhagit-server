package inha.git.admin.api.service;

import inha.git.admin.api.controller.dto.request.UserRankingScoreRequest;
import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.domain.QuestionComment;
import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.user.api.service.UserRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class AdminScoreServiceImpl implements AdminScoreService {

    private final UserJpaRepository userJpaRepository;
    private final UserRankingService userRankingService;

    /**
     * 관리자 점수 수정
     *
     * @param userRankingScoreRequest   admin Score 수정 요청
     * @return UpdateUserRankingResponse
     *
     */
    public UserRankingResponse updateScore(UserRankingScoreRequest userRankingScoreRequest) {
        User user = userJpaRepository.findById(userRankingScoreRequest.userIdx())
                        .orElseThrow(() -> new BaseException(NOT_FIND_USER));

        userRankingService.updateAdminScore(user, userRankingScoreRequest.adminScore());
        return userRankingService.getUserRanking(user);
    }
}

package inha.git.user.api.service;

import inha.git.user.api.controller.dto.response.UserRankingResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.UserRanking;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.user.domain.repository.UserRankingJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserRankingServiceImpl implements UserRankingService {

    private final UserJpaRepository userJpaRepository;
    private final UserRankingJpaRepository userRankingJpaRepository;

    /**
     * 모든 사용자 랭킹 점수 (totalScore) 재계산 - 하루에 한번
     * @return 사용자 별 총점 반환
     */
    @Override
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateAllTotalScores(){
        List<User> users = userJpaRepository.findAll();

        for(User user : users){
            if (user.getRole() == Role.USER) {
                userRankingJpaRepository.findByUser(user).ifPresent(userRanking -> {
                    int total = calculateTotalScore(userRanking);
                    userRanking.setTotalScore(total);
                });
            }
        }
        log.info("모든 사용자 totalScore 업데이트 완료 (총 사용자 수: {})", users.size());
    }

    /**
     * 사용자 접수 항목들을 기반으로 총점 계산
     * @Param 사용자들의 ranking 정보
     * @return 사용자 별 총점 반환
     * */
    private int calculateTotalScore(UserRanking userRanking){
        int loginCount = userRanking.getLoginCount();
        int githubNoncurricularCount = userRanking.getGithubNoncurricularCount();
        int localNoncurricularCount = userRanking.getLocalNoncurricularCount();
        int githubCurricularCount = userRanking.getGithubCurricularCount();
        int localCurricularCount = userRanking.getLocalCurricularCount();
        int questionCount = userRanking.getQuestionCount();
        int questionCommentCount = userRanking.getQuestionCommentCount();
        int patentProgramCount = userRanking.getPatentProgramCount();
        int likeCount = userRanking.getLikeCount();
        int recommendCount = userRanking.getRecommendCount();
        int projectStarCount = userRanking.getProjectStarCount();
        int problemParticipationCount = userRanking.getProblemParticipationCount();
        int adminScore = userRanking.getAdminScore();

        return
                (githubNoncurricularCount*5) +
                (localNoncurricularCount*5) +
                (githubCurricularCount*5) +
                (localCurricularCount*5) +
                (questionCount) +
                (questionCommentCount) +
                (patentProgramCount*3) +
                (likeCount) +
                (projectStarCount*10) +
                (adminScore);
    }

    /**
     * 관리자 점수 수동 업데이트
     * @Param 사용자, 관리자 점수
     */
    @Override
    @Transactional
    public void updateAdminScore(User user, int newAdminScore) {
        userRankingJpaRepository.findByUser(user).ifPresentOrElse(
                userRanking -> {
                    userRanking.setAdminScore(newAdminScore);
                    int total = calculateTotalScore(userRanking);
                    userRanking.setTotalScore(total); // totalScore도 업데이트
                },
                () -> {
                    throw new IllegalArgumentException("해당 유저의 랭킹 정보가 없습니다.");
                });
    }

    /**
     * 특정 사용자에 대한 랭킹 점수 반환
     * @return 특정 사용자의 랭킹 점수
     */
    @Override
    public UserRankingResponse getUserRanking(User user) {
        return userRankingJpaRepository.findByUser(user)
                .map(UserRankingResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 랭킹 정보가 없습니다."));
    }

    /**
     * 사용자 totalScore 기반으로 순위 반환
     * @return 10위까지의 사용자 정보 반환
     * */

    @Override
    public List<UserRankingResponse> getUserRankings() {
        List<UserRanking> rankings = userRankingJpaRepository.findTop10ByOrderByTotalScoreDesc();
        return rankings.stream()
                .map(UserRankingResponse::from)
                .toList(); // 또는 .collect(Collectors.toList())
    }

}

package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.mapper.ProblemSubmitMapper;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemRequestJpaRepository;
import inha.git.problem.domain.repository.ProblemSubmitJpaRepository;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.UserRanking;
import inha.git.user.domain.repository.UserRankingJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("문제 제출 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProblemSubmitServiceTest {

    private static final Integer PROBLEM_IDX = 1;
    private static final Integer PROJECT_IDX = 10;

    @InjectMocks
    private ProblemSubmitServiceImpl problemSubmitService;

    @Mock
    private ProblemJpaRepository problemJpaRepository;

    @Mock
    private ProblemRequestJpaRepository problemRequestJpaRepository;

    @Mock
    private ProblemSubmitJpaRepository problemSubmitJpaRepository;

    @Mock
    private ProblemSubmitMapper problemSubmitMapper;

    @Mock
    private UserRankingJpaRepository userRankingJpaRepository;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    private User user;
    private ProblemRequest problemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).name("제출자").build();
        problemRequest = ProblemRequest.builder()
                .id(100)
                .problemRequestStatus(ProblemRequestStatus.APPROVAL)
                .user(user)
                .build();
    }

    private void givenApprovedRequestNotSubmitted() {
        given(problemRequestJpaRepository.findByProblemIdAndUserAndState(PROBLEM_IDX, user, ACTIVE))
                .willReturn(Optional.of(problemRequest));
        given(problemSubmitJpaRepository.existsByProblemRequest(problemRequest)).willReturn(false);
    }

    private void assertNothingChanged() {
        verify(problemSubmitJpaRepository, never()).save(any());
        verify(problemRequestJpaRepository, never()).save(any());
        verify(userRankingJpaRepository, never()).save(any());
        assertThat(problemRequest.getProblemRequestStatus()).isEqualTo(ProblemRequestStatus.APPROVAL);
    }

    @Test
    @DisplayName("projectIdx가 없으면 PROBLEM_SUBMIT_PROJECT_REQUIRED 예외")
    void problemSubmit_ProjectIdxNull_Fail() {
        // given
        givenApprovedRequestNotSubmitted();

        // when & then
        assertThatThrownBy(() -> problemSubmitService.problemSubmit(user, PROBLEM_IDX, null))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorReason().code())
                .isEqualTo(PROBLEM_SUBMIT_PROJECT_REQUIRED.getCode());
        verify(projectJpaRepository, never()).findByIdAndState(any(), any());
        assertNothingChanged();
    }

    @Test
    @DisplayName("존재하지 않거나 삭제된 게시글이면 PROBLEM_SUBMIT_PROJECT_NOT_FOUND 예외")
    void problemSubmit_ProjectNotFound_Fail() {
        // given
        givenApprovedRequestNotSubmitted();
        given(projectJpaRepository.findByIdAndState(999999, ACTIVE)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> problemSubmitService.problemSubmit(user, PROBLEM_IDX, 999999))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorReason().code())
                .isEqualTo(PROBLEM_SUBMIT_PROJECT_NOT_FOUND.getCode());
        assertNothingChanged();
    }

    @Test
    @DisplayName("다른 사용자가 작성한 게시글이면 PROBLEM_SUBMIT_PROJECT_NOT_OWNED 예외")
    void problemSubmit_ProjectNotOwned_Fail() {
        // given
        givenApprovedRequestNotSubmitted();
        User other = User.builder().id(2).name("다른사용자").build();
        Project othersProject = Project.builder().id(PROJECT_IDX).user(other).build();
        given(projectJpaRepository.findByIdAndState(PROJECT_IDX, ACTIVE)).willReturn(Optional.of(othersProject));

        // when & then
        assertThatThrownBy(() -> problemSubmitService.problemSubmit(user, PROBLEM_IDX, PROJECT_IDX))
                .isInstanceOf(BaseException.class)
                .extracting(e -> ((BaseException) e).getErrorReason().code())
                .isEqualTo(PROBLEM_SUBMIT_PROJECT_NOT_OWNED.getCode());
        assertNothingChanged();
    }

    @Test
    @DisplayName("본인 게시글이면 제출 성공 - 제출 저장, 신청 COMPLETE, 참여 횟수 증가")
    void problemSubmit_Success() {
        // given
        givenApprovedRequestNotSubmitted();
        Project myProject = Project.builder().id(PROJECT_IDX).user(user).build();
        given(projectJpaRepository.findByIdAndState(PROJECT_IDX, ACTIVE)).willReturn(Optional.of(myProject));

        ProblemSubmit problemSubmit = ProblemSubmit.builder().id(1000).projectId(PROJECT_IDX).build();
        given(problemSubmitMapper.toProblemSubmit(PROJECT_IDX, problemRequest)).willReturn(problemSubmit);

        UserRanking userRanking = UserRanking.builder().user(user).problemParticipationCount(0).build();
        given(userRankingJpaRepository.findByUser(user)).willReturn(Optional.of(userRanking));

        ProblemSubmitResponse expected = new ProblemSubmitResponse(1000);
        given(problemSubmitMapper.toProblemSubmitResponse(problemSubmit)).willReturn(expected);

        // when
        ProblemSubmitResponse response = problemSubmitService.problemSubmit(user, PROBLEM_IDX, PROJECT_IDX);

        // then
        assertThat(response).isEqualTo(expected);
        verify(problemSubmitJpaRepository).save(problemSubmit);
        verify(problemRequestJpaRepository).save(problemRequest);
        assertThat(problemRequest.getProblemRequestStatus()).isEqualTo(ProblemRequestStatus.COMPLETE);
        assertThat(userRanking.getProblemParticipationCount()).isEqualTo(1);
    }
}

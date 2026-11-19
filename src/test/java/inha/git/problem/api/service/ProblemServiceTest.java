package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProblemField;
import inha.git.mapping.domain.repository.ProblemFieldJpaRepository;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.mapper.ProblemMapper;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.problem.domain.repository.ProblemAttachmentJpaRepository;
import inha.git.problem.domain.repository.ProblemJpaRepository;
import inha.git.problem.domain.repository.ProblemQueryRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.NOT_AUTHORIZED_PROBLEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("문제 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final Integer PROBLEM_IDX = 1;
    private static final Integer FIELD_IDX = 1;

    @InjectMocks
    private ProblemServiceImpl problemService;

    @Mock
    private ProblemJpaRepository problemJpaRepository;

    @Mock
    private ProblemAttachmentJpaRepository problemAttachmentJpaRepository;

    @Mock
    private ProblemFieldJpaRepository problemFieldJpaRepository;

    @Mock
    private FieldJpaRepository fieldJpaRepository;

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private ProblemQueryRepository problemQueryRepository;

    @Mock
    private IdempotentProvider idempotentProvider;

    private static String daysFromToday(int days) {
        return LocalDate.now(KST).plusDays(days).toString();
    }

    private User createAuthor() {
        return User.builder().id(1).name("출제자").role(Role.PROFESSOR).build();
    }

    private Problem createProblem(User author, ProblemStatus status, String duration) {
        Problem problem = Problem.builder()
                .id(PROBLEM_IDX)
                .title("문제 제목")
                .contents("문제 내용")
                .duration(duration)
                .status(status)
                .user(author)
                .build();
        Field field = Field.builder().id(FIELD_IDX).name("분야").build();
        problem.getProblemFields().add(ProblemField.builder().problem(problem).field(field).build());
        return problem;
    }

    private UpdateProblemRequest createUpdateRequest(String duration) {
        return new UpdateProblemRequest("문제 제목", duration, "문제 내용", List.of(FIELD_IDX));
    }

    @Nested
    @DisplayName("마감일 지난 문제 상태 일괄 변경 스케줄러")
    class CompleteExpiredProblems {

        @Test
        @DisplayName("오늘(KST) 날짜 기준으로 PROGRESS인 활성 문제를 COMPLETED로 벌크 변경")
        void completeExpiredProblems_Success() {
            // given
            String today = LocalDate.now(KST).toString();
            given(problemJpaRepository.bulkUpdateStatusByDurationBefore(
                    eq(today), eq(ProblemStatus.PROGRESS), eq(ProblemStatus.COMPLETED), eq(ACTIVE), any(LocalDateTime.class)))
                    .willReturn(3);

            // when
            problemService.completeExpiredProblems();

            // then
            verify(problemJpaRepository).bulkUpdateStatusByDurationBefore(
                    eq(today), eq(ProblemStatus.PROGRESS), eq(ProblemStatus.COMPLETED), eq(ACTIVE), any(LocalDateTime.class));
            verify(problemJpaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("문제 수정 시 마감일 연장에 따른 상태 변경")
    class UpdateProblemStatusByDuration {

        @Test
        @DisplayName("COMPLETED 문제의 마감일을 미래로 연장하면 PROGRESS로 되돌림")
        void updateProblem_DeadlineExtendedToFuture_Success() {
            // given
            User author = createAuthor();
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, daysFromToday(-3));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(daysFromToday(7)), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.PROGRESS);
        }

        @Test
        @DisplayName("COMPLETED 문제의 마감일을 오늘로 연장하면 PROGRESS로 되돌림 (마감일 당일은 진행중)")
        void updateProblem_DeadlineExtendedToToday_Success() {
            // given
            User author = createAuthor();
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, daysFromToday(-1));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(daysFromToday(0)), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.PROGRESS);
        }

        @Test
        @DisplayName("마감일을 연장했지만 여전히 지난 날짜면 COMPLETED 유지")
        void updateProblem_DeadlineExtendedButStillPast_Success() {
            // given
            User author = createAuthor();
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, daysFromToday(-10));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(daysFromToday(-1)), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.COMPLETED);
        }

        @Test
        @DisplayName("마감일이 그대로면 출제자가 수동으로 바꾼 COMPLETED 유지")
        void updateProblem_DeadlineUnchanged_Success() {
            // given
            User author = createAuthor();
            String duration = daysFromToday(7);
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, duration);
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(duration), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.COMPLETED);
        }

        @Test
        @DisplayName("마감일을 앞당기면(미래라도) COMPLETED 유지")
        void updateProblem_DeadlineShortened_Success() {
            // given
            User author = createAuthor();
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, daysFromToday(10));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(daysFromToday(5)), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.COMPLETED);
        }

        @Test
        @DisplayName("UNDER_REVIEW(심사중) 문제는 마감일을 연장해도 상태 유지")
        void updateProblem_UnderReviewDeadlineExtended_Success() {
            // given
            User author = createAuthor();
            Problem problem = createProblem(author, ProblemStatus.UNDER_REVIEW, daysFromToday(-3));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when
            problemService.updateProblem(author, PROBLEM_IDX, createUpdateRequest(daysFromToday(7)), null);

            // then
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.UNDER_REVIEW);
        }

        @Test
        @DisplayName("출제자도 관리자도 아니면 수정 불가 - 상태 변경 없음")
        void updateProblem_NotAuthor_ThrowsException() {
            // given
            User author = createAuthor();
            User other = User.builder().id(2).name("다른교수").role(Role.PROFESSOR).build();
            Problem problem = createProblem(author, ProblemStatus.COMPLETED, daysFromToday(-3));
            given(problemJpaRepository.findByIdAndState(PROBLEM_IDX, ACTIVE)).willReturn(Optional.of(problem));

            // when & then
            assertThatThrownBy(() -> problemService.updateProblem(other, PROBLEM_IDX, createUpdateRequest(daysFromToday(7)), null))
                    .isInstanceOf(BaseException.class)
                    .extracting(e -> ((BaseException) e).getErrorReason().code())
                    .isEqualTo(NOT_AUTHORIZED_PROBLEM.getCode());
            assertThat(problem.getStatus()).isEqualTo(ProblemStatus.COMPLETED);
            verify(problemJpaRepository, never()).saveAndFlush(any());
        }
    }
}

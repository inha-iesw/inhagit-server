package inha.git.problem.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.enums.ProblemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ProblemJpaRepository는 Problem 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemJpaRepository extends JpaRepository<Problem, Integer> {
    Optional<Problem> findByIdAndState(Integer problemIdx, State state);

    /**
     * 마감일(duration)이 기준일보다 이전인 문제의 상태를 일괄 변경
     *
     * <p>duration은 "yyyy-MM-dd" 형식의 문자열이므로 사전순 비교가 날짜순 비교와 같다.</p>
     *
     * @param today      기준일 ("yyyy-MM-dd")
     * @param fromStatus 변경 대상 상태
     * @param toStatus   변경할 상태
     * @param state      엔티티 상태
     * @param now        수정 시각 (벌크 UPDATE는 @LastModifiedDate가 적용되지 않으므로 직접 지정)
     * @return 변경된 문제 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Problem p SET p.status = :toStatus, p.updatedAt = :now " +
            "WHERE p.status = :fromStatus AND p.state = :state AND p.duration < :today")
    int bulkUpdateStatusByDurationBefore(@Param("today") String today,
                                         @Param("fromStatus") ProblemStatus fromStatus,
                                         @Param("toStatus") ProblemStatus toStatus,
                                         @Param("state") State state,
                                         @Param("now") LocalDateTime now);
}

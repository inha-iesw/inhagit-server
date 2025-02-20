package inha.git.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity는 모든 엔티티의 기본 속성을 정의하는 추상 클래스.
 * 생성 일자, 수정 일자, 상태를 포함.
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity {

    /**
     * 엔티티가 생성된 일자로 수정 불가능.
     */
    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티가 마지막으로 수정된 일자.
     */
    @LastModifiedDate
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    /*
     * 엔티티가 삭제된 일자.
    */
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    /**
     * 엔티티의 상태로 ACTIVE or INACTIVE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    protected State state = State.ACTIVE;

    /**
     * 엔티티의 상태를 정의하는 열거형.
     */
    public enum State {
        ACTIVE, INACTIVE
    }

    /**
     * 엔티티의 상태를 설정.
     *
     * @param state 설정할 상태
     */
    public void setState(State state) {
        this.state = state;
    }

    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }
}

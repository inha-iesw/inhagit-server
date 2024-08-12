package inha.git.assistant.domain;

import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * Assistant 엔티티는 애플리케이션의 조교 승격 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "assistant_tb")
public class Assistant {

    @Id
    @Column(name = "assistant_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accept_user_id")
    private User user;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}

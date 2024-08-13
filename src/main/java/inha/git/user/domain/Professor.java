package inha.git.user.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Professor 엔티티는 애플리케이션의 교수 승인 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "professor_tb")
public class Professor {

    @Id
    @Column(name = "professor_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
}
package inha.git.mapping.domain;

import inha.git.mapping.domain.id.TeamUserId;
import inha.git.team.domain.Team;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * TeamUser 엔티티는 애플리케이션의 팀과 유저 매핑 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "team_user_tb")
public class TeamUser {

    @EmbeddedId
    private TeamUserId id;

    @MapsId("teamId")
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}

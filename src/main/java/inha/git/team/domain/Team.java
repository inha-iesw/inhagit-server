package inha.git.team.domain;

import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * Team 엔티티는 애플리케이션의 팀 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "team_tb")
public class Team extends BaseEntity {

    @Id
    @Column(name = "team_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(nullable = false, length = 12)
    private String name;

    @Setter
    @Column(nullable = false, name = "max_member_number")
    private Integer maxMemberNumber;

    @Column(nullable = false, name = "current_member_number")
    private Integer currtentMemberNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void increaseCurrentMemberNumber() {
        this.currtentMemberNumber++;
    }

    public void decreaseCurrentMemberNumber() {
        this.currtentMemberNumber--;
    }
}

package inha.git.user.domain;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "email_domain_tb")
public class EmailDomain {

    @Id
    @Column(name = "email_domain_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, name = "email_domain")
    private String emailDomain;

    @Column(nullable = false, name = "user_type")
    private Integer userType;
}

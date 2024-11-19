package inha.git.user.domain;

import inha.git.common.BaseEntity;
import inha.git.department.domain.Department;
import inha.git.mapping.domain.UserDepartment;
import inha.git.notice.domain.Notice;
import inha.git.project.domain.Project;
import inha.git.user.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User 엔티티는 애플리케이션의 사용자 정보를 나타냄.
 * 이 클래스는 Spring Security의 UserDetails 인터페이스를 구현하여 사용자 인증 및 권한 부여에 사용.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "user_tb")
public class User extends BaseEntity implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 100)
    private String pw;

    @Column(nullable = false, length = 12)
    private String name;

    @Column(length = 8,name = "user_number")
    private String userNumber;

    @Column(length = 150, name = "github_token")
    private String githubToken;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Column(name = "report_count")
    private int reportCount;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserDepartment> userDepartments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Company company;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Professor professor;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }
    @Override
    public String getPassword() {
        return pw;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String pw) {
        this.pw = pw;
    }

    // 연관관계 편의 메서드
    public void addDepartment(Department department) {
        UserDepartment userDepartment = new UserDepartment(this, department);
        userDepartments.add(userDepartment);
        department.getUserDepartments().add(userDepartment);
    }

    public void addNotice(Notice notice) {
        notices.add(notice);
        if (notice.getUser() != this) {
            notice.setUser(this);  // 양방향 연관관계 설정
        }
    }


    public void setCompany(Company company) {
        this.company = company;
        if (company.getUser() != this) {
            company.setUser(this);  // 양방향 연관관계 설정
        }
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setProssor(Professor professor) {
        this.professor = professor;
        if (professor.getUser() != this) {
            professor.setUser(this);  // 양방향 연관관계 설정
        }
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

    public void decreaseReportCount() {
        if(this.reportCount > 0)
            this.reportCount--;
    }
}

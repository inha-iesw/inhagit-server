package inha.git.field.domain;

import inha.git.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


/**
 * Field 엔티티는 애플리케이션의 필드 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "field_tb")
public class Field extends BaseEntity {

    @Id
    @Column(name = "field_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;

}

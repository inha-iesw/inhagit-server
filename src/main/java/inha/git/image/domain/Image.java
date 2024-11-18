package inha.git.image.domain;

import inha.git.common.BaseEntity;
import inha.git.report.domain.ReportReason;
import inha.git.report.domain.ReportType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Image 엔티티는 애플리케이션의 이미지 정보를 나타냄.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@Entity
@Table(name = "image_tb")
public class Image extends BaseEntity {


    @Id
    @Column(name = "image_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200, name = "image_url")
    private String imageUrl;
}

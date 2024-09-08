package inha.git.statistics.domain.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * CollegeStatisticsStatisticsId CollegeStatisticsStatistics 엔티티의 복합키를 나타냄.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CollegeStatisticsStatisticsId implements Serializable {

    private Integer collegeId;

    private Integer semesterId;

    private Integer fieldId;
}
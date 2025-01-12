package inha.git.statistics.domain.enums;

public enum StatisticsType {
    TOTAL("전체", null),
    COLLEGE("단과대", "college_id"),
    DEPARTMENT("학과", "department_id"),
    USER("사용자", "user_id");

    private final String description;
    private final String idColumnName;

    StatisticsType(String description, String idColumnName) {
        this.description = description;
        this.idColumnName = idColumnName;
    }

    public String getDescription() {
        return description;
    }

    public String getIdColumnName() {
        return idColumnName;
    }
}

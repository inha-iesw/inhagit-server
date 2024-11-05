package inha.git.statistics.domain;

public enum StatisticsType {
    TOTAL("전체"),
    COLLEGE("단과대"),
    DEPARTMENT("학과");

    private String prefix;

    StatisticsType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

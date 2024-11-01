package inha.git.statistics.domain;

public enum StatisticsType {
    TOTAL("전체"),
    COLLEGE("단과대별"),
    DEPARTMENT("학과별");

    private String prefix;

    StatisticsType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}

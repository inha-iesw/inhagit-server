package inha.git.search.domain.enums;

public enum TableType {

    I_FOSS("I-FOSS"),
    PROBLEM("문제"),
    ISSS("ISSS"),
    TEAM("팀"),
    NOTICE("공지");

    private final String value;

    TableType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

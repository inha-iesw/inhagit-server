package inha.git.bug_report.domain.enums;

public enum ReportType {

    BUG("오류"), INQUIRY("문의");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

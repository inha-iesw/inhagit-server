package inha.git.bug_report.domain.enums;

public enum BugStatus {
    UNCONFIRMED("확인 전"), CONFIRMING("확인 중"), CONFIRMED("해결 완료");

    private String status;

    BugStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

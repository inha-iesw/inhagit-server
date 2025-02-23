package inha.git.problem.domain.enums;

public enum ProblemRequestStatus {
    REQUEST("신청중"), APPROVAL("승인"), REJECTION("거절"), COMPLETE("완료");

    private final String status;

    ProblemRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

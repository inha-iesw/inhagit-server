package inha.git.problem.domain.enums;

public enum ProblemStatus {
    PROGRESS("진행중"), UNDER_REVIEW("심사중"), COMPLETED("완료");

    private final String status;

    ProblemStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

package inha.git.problem.domain.enums;

public enum Grade {

    FIRST("1학년"), SECOND("2학년"), THIRD("3학년"), FOURTH("4학년"), EXCESS("초과학기");

    private final String grade;

    Grade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }
}

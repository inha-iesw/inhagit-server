package inha.git.project.domain.enums;

public enum PatentType {

    PATENT("특허"), PROGRAM("프로그램");

    private final String description;

    PatentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

package com.assignment3.project.enums;

public enum UserRole {
    NEEDS_HELP("Нуждающийся"),
    DONOR("Донор"),
    ADMIN("Администратор");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


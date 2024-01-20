package org.studyeasy.SpringBlog.util.constants;

public enum Roles {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), EDITOR("ROLE_EDITOR");

    private final String role;

    private Roles(final String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}

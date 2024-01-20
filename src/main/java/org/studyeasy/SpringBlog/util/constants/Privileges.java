package org.studyeasy.SpringBlog.util.constants;

public enum Privileges {
    RESET_ANY_USER_PASSWORD(1L, "RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2L, "ACCESS_ADMIN_PANEL");

    private final Long id;
    private final String privilege;

    private Privileges(final Long id, final String privilege) {
        this.id = id;
        this.privilege = privilege;
    }

    public Long getId() {
        return this.id;
    }

    public String getPrivilege() {
        return this.privilege;
    }
}

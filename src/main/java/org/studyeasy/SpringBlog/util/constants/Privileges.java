package org.studyeasy.SpringBlog.util.constants;

import lombok.Getter;

@Getter
public enum Privileges {
    RESET_ANY_USER_PASSWORD(1L, "RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2L, "ACCESS_ADMIN_PANEL");

    private final Long id;
    private final String privilege;

    Privileges(final Long id, final String privilege) {
        this.id = id;
        this.privilege = privilege;
    }

}

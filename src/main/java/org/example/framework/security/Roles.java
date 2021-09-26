package org.example.framework.security;

public class Roles {
    private Roles() {
    }

    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    public static final String ROLE_USER = "ROLE_USER";

    public static String getById(long i) {
        return switch ((int) i) {
            // okay
            case 1 -> ROLE_ADMIN;
            case 2 -> ROLE_MODERATOR;
            case 3 -> ROLE_USER;
            default -> ROLE_ANONYMOUS;
        };

    }

}

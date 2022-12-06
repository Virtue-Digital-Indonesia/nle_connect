package com.nle.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";
    public static final String RESET_PASSWORD = "RESET_PASSWORD";
    public static final String IMPERSONATE_DEPO = "ROLE_PREVIOUS_ADMINISTRATOR,DEPO_OWNER";
    public static final String BOOKING_CUSTOMER = "BOOKING_CUSTOMER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}

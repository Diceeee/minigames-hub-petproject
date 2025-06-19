package com.dice.auth.core.access;

public interface Authorities {

    /**
     * Has access to complete email verification, for new users only.
     */
    String VERIFY_EMAIL = "VERIFY_EMAIL";
}

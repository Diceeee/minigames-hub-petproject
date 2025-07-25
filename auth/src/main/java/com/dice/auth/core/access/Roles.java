package com.dice.auth.core.access;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Roles {

    USER, ADMIN;

    final String roleName;

    Roles() {
        this.roleName = name();
    }

    public String getRoleWithoutPrefix() {
        return roleName;
    }

    public String getRoleWithPrefix() {
        return "ROLE_" + getRoleWithoutPrefix();
    }
}

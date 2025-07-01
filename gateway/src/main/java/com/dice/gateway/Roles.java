package com.dice.gateway;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Roles {

    USER, ADMIN;

    String roleName;

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

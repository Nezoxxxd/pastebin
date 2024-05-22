package com.project.pastebin.entities;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN("ADMIN"),
    ROLE_USER("USER");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

}

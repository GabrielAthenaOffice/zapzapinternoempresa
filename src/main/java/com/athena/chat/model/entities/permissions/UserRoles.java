package com.athena.chat.model.entities.permissions;

public enum UserRoles {
    ADMIN ("admin"),
    FUNCIONARIO ("funcionario");

    private String role;

    UserRoles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}

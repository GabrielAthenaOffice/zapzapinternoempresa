package com.athena.chat.model.entities.permissions;

public enum UserRole {
    ADMIN ("admin"),
    FUNCIONARIO ("funcionario");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}

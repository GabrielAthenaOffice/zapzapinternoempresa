package com.athena.chat.model.entities.permissions;

import java.util.Set;
import java.util.HashSet;

public enum UserRole {
    ADMIN("admin"),
    LIDER_DE_SETOR("lider_de_setor"),
    FUNCIONARIO("funcionario"),
    ESTAGIARIO("estagiario");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    /**
     * Retorna o conjunto de permissões associadas a cada role.
     * Implementa hierarquia: ADMIN > LIDER_DE_SETOR > FUNCIONARIO > ESTAGIARIO
     */
    public Set<Permission> getPermissions() {
        Set<Permission> permissions = new HashSet<>();

        switch (this) {
            case ADMIN:
                // ADMIN tem todas as permissoes
                permissions.add(Permission.USER_CREATE);
                permissions.add(Permission.USER_READ);
                permissions.add(Permission.USER_UPDATE);
                permissions.add(Permission.USER_DELETE);
                permissions.add(Permission.GROUP_CREATE);
                permissions.add(Permission.GROUP_READ);
                permissions.add(Permission.GROUP_UPDATE);
                permissions.add(Permission.GROUP_DELETE);
                permissions.add(Permission.GROUP_MANAGE_MEMBERS);
                permissions.add(Permission.MESSAGE_READ);
                permissions.add(Permission.MESSAGE_SEND);
                permissions.add(Permission.MESSAGE_DELETE);
                permissions.add(Permission.ROLE_MANAGE);
                break;

            case LIDER_DE_SETOR:
                // LIDER_DE_SETOR tem permissoes amplas, mas nao pode deletar usuarios ou
                // gerenciar roles
                permissions.add(Permission.USER_CREATE);
                permissions.add(Permission.USER_READ);
                permissions.add(Permission.USER_UPDATE);
                permissions.add(Permission.GROUP_CREATE);
                permissions.add(Permission.GROUP_READ);
                permissions.add(Permission.GROUP_UPDATE);
                permissions.add(Permission.GROUP_DELETE);
                permissions.add(Permission.GROUP_MANAGE_MEMBERS);
                permissions.add(Permission.MESSAGE_READ);
                permissions.add(Permission.MESSAGE_SEND);
                permissions.add(Permission.MESSAGE_DELETE);
                break;

            case FUNCIONARIO:
                // FUNCIONARIO tem permissoes operacionais basicas
                permissions.add(Permission.USER_READ);
                permissions.add(Permission.GROUP_CREATE);
                permissions.add(Permission.GROUP_READ);
                permissions.add(Permission.MESSAGE_READ);
                permissions.add(Permission.MESSAGE_SEND);
                break;

            case ESTAGIARIO:
                // ESTAGIARIO tem permissoes mais restritas
                permissions.add(Permission.USER_READ);
                permissions.add(Permission.GROUP_READ);
                permissions.add(Permission.MESSAGE_READ);
                permissions.add(Permission.MESSAGE_SEND);
                break;
        }

        return permissions;
    }
}

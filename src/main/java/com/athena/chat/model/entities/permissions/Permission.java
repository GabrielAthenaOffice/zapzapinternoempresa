package com.athena.chat.model.entities.permissions;

/**
 * Enum que define todas as permissões granulares do sistema.
 * Cada permissão representa uma ação específica que pode ser controlada.
 */
public enum Permission {
    // Permissões de Usuário
    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    // Permissões de Grupo
    GROUP_CREATE("group:create"),
    GROUP_READ("group:read"),
    GROUP_UPDATE("group:update"),
    GROUP_DELETE("group:delete"),
    GROUP_MANAGE_MEMBERS("group:manage_members"),

    // Permissões de Mensagem
    MESSAGE_READ("message:read"),
    MESSAGE_SEND("message:send"),
    MESSAGE_DELETE("message:delete"),

    // Permissões de Gerenciamento
    ROLE_MANAGE("role:manage");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

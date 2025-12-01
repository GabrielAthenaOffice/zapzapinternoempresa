package com.athena.chat.services;

import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;
import com.athena.chat.model.entities.permissions.Permission;
import com.athena.chat.model.entities.permissions.UserRole;
import com.athena.chat.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço responsável por verificar permissões de usuários.
 * Centraliza toda a lógica de autorização granular do sistema.
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final GroupRepository groupRepository;

    /**
     * Verifica se o usuário possui uma permissão específica.
     */
    public boolean hasPermission(User user, Permission permission) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        Set<Permission> userPermissions = user.getRole().getPermissions();
        return userPermissions.contains(permission);
    }

    /**
     * Verifica se o usuário possui pelo menos uma das permissões especificadas.
     */
    public boolean hasAnyPermission(User user, Permission... permissions) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        Set<Permission> userPermissions = user.getRole().getPermissions();
        for (Permission permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se o usuário é o criador de um grupo específico.
     */
    public boolean isGroupCreator(User user, Long groupId) {
        if (user == null || groupId == null) {
            return false;
        }

        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return false;
        }

        return group.getCriadoPor() != null &&
                group.getCriadoPor().getId().equals(user.getId());
    }

    /**
     * Verifica se o usuário pode gerenciar um grupo (criador ou ADMIN).
     */
    public boolean canManageGroup(User user, Long groupId) {
        if (user == null || groupId == null) {
            return false;
        }

        // ADMIN sempre pode gerenciar
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Verifica se é o criador do grupo
        return isGroupCreator(user, groupId);
    }

    /**
     * Verifica se o usuário pode remover membros de um grupo.
     * Apenas o criador do grupo ou ADMIN podem remover membros.
     */
    public boolean canRemoveGroupMember(User user, Long groupId) {
        return canManageGroup(user, groupId);
    }

    /**
     * Verifica se o usuário pode atualizar um grupo.
     * Apenas o criador do grupo ou ADMIN podem atualizar.
     */
    public boolean canUpdateGroup(User user, Long groupId) {
        return canManageGroup(user, groupId);
    }

    /**
     * Verifica se o usuário pode deletar um grupo.
     * Apenas o criador do grupo ou ADMIN podem deletar.
     */
    public boolean canDeleteGroup(User user, Long groupId) {
        return canManageGroup(user, groupId);
    }
}

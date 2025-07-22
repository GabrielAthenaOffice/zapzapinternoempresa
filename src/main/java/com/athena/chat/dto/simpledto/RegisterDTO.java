package com.athena.chat.dto.simpledto;

import com.athena.chat.model.entities.permissions.UserRoles;

public record RegisterDTO(String nome, String email, String senha, String cargo, UserRoles roles) {
}

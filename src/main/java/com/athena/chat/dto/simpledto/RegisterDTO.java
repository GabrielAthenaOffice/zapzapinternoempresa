package com.athena.chat.dto.simpledto;

import com.athena.chat.model.entities.permissions.UserRole;

public record RegisterDTO(String nome, String email, String senha, String cargo, UserRole roles) {
}

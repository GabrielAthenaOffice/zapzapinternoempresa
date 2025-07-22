package com.athena.chat.dto.mapper;


import com.athena.chat.dto.GroupDTO;
import com.athena.chat.dto.simpledto.GroupSimpleDTO;
import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;

import java.util.stream.Collectors;

public class GroupMapper {

    public static GroupDTO toDTO(Group group) {
        return new GroupDTO(
                group.getId(),
                group.getNome(),
                group.getDescricao(),
                group.getCriadoPor() != null ? group.getCriadoPor().getNome() : null,
                group.getCriadoEm(),
                group.getMembros().stream()
                        .map(User::getNome)
                        .collect(Collectors.toList())
        );
    }

    public static GroupSimpleDTO toSimpleDTO(Group group) {
        return new GroupSimpleDTO(
                group.getId(),
                group.getNome()
        );
    }
}

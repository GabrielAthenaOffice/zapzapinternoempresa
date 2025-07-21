package com.athena.chat.dto.mapper;

import com.athena.chat.dto.chat.ChatDocumentDTO;
import com.athena.chat.model.sql.ChatDocumentSQL;

public class ChatDocumentMapper {

    public static ChatDocumentDTO toDTO(ChatDocumentSQL doc) {
        return new ChatDocumentDTO(
                doc.getId(),
                doc.getFileName(),
                doc.getFileType(),
                doc.getFileUrl(),
                doc.getSender() != null ? doc.getSender().getId() : null,
                doc.getGroup() != null ? doc.getGroup().getId() : null,
                doc.getUploadedAt()
        );
    }
}

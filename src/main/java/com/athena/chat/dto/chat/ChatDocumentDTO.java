package com.athena.chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDocumentDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long senderId;
    private Long groupId;
    private LocalDateTime uploadedAt;
}

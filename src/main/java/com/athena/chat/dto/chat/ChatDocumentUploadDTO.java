package com.athena.chat.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDocumentUploadDTO {
    private Long senderId;
    private Long groupId;
}

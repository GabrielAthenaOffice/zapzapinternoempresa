package com.athena.chat.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private Long groupId;
    private Long senderId;
    private String content;
    private String type; // texto, imagem, arquivo
    private LocalDateTime timestamp = LocalDateTime.now();
}


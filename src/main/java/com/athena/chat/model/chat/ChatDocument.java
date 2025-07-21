package com.athena.chat.model.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "mensagens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDocument {
    @Id
    private String id;

    private Long groupId;      // ID do grupo (referência no PostgreSQL)
    private Long senderId;     // ID do usuário que enviou
    private String content;
    private String type;       // texto, imagem, arquivo
    private String status = "ENVIADA";
    private LocalDateTime timestamp = LocalDateTime.now();
}
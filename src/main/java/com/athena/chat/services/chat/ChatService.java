package com.athena.chat.services.chat;
import com.athena.chat.dto.chat.ChatMessageDTO;
import com.athena.chat.model.chat.ChatDocument;
import com.athena.chat.repositories.chat.ChatDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatDocumentRepository chatDocumentRepository;

    public ChatDocument salvarMensagem(ChatMessageDTO messageDTO) {
        ChatDocument doc = new ChatDocument();
        doc.setGroupId(messageDTO.getGroupId());
        doc.setSenderId(messageDTO.getSenderId());
        doc.setContent(messageDTO.getContent());
        doc.setType(messageDTO.getType());
        doc.setTimestamp(LocalDateTime.now());
        return chatDocumentRepository.save(doc);
    }

    public List<ChatDocument> buscarHistorico(Long groupId) {
        return chatDocumentRepository.findByGroupId(groupId);
    }
}
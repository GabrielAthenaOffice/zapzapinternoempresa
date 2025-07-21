package com.athena.chat.controller.chat;

import com.athena.chat.dto.chat.ChatMessageDTO;
import com.athena.chat.model.chat.ChatDocument;
import com.athena.chat.model.chat.ChatMessage;
import com.athena.chat.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{groupId}")
    public List<ChatDocument> getMessages(@PathVariable Long groupId) {
        return chatService.buscarHistorico(groupId);
    }

    @PostMapping
    public ResponseEntity<ChatDocument> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        ChatDocument saved = chatService.salvarMensagem(messageDTO);
        return ResponseEntity.ok(saved);
    }
}
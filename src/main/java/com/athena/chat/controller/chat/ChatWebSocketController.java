package com.athena.chat.controller.chat;


import com.athena.chat.dto.chat.ChatMessageDTO;
import com.athena.chat.model.chat.ChatDocument;
import com.athena.chat.model.chat.ChatMessage;
import com.athena.chat.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat")    // cliente envia para /app/chat
    @SendTo("/topic/messages")  // mensagens broadcast em /topic/messages
    public ChatDocument sendMessage(ChatMessageDTO message) {
        return chatService.salvarMensagem(message);
    }
}

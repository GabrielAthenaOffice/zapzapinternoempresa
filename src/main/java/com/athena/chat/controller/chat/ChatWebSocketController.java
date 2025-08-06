package com.athena.chat.controller.chat;


import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.services.chat.ChatService;
import com.athena.chat.services.chat.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chats/{chatId}/send") // Ex: /app/chats/1/send
    public void enviarMensagem(@DestinationVariable Long chatId, MensagemDTO mensagemDTO, Principal principal) {
        MensagemDTO mensagemSalva = chatService.salvarMensagem(chatId, mensagemDTO, principal.getName());

        // Envia mensagem para todos os inscritos no /topic/chats/{chatId}
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, mensagemSalva);
    }
}

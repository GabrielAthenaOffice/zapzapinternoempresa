package com.athena.chat.controller.chat;


import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.services.chat.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensagemService mensagemService;

    @MessageMapping("/chat/enviar") // cliente envia para /app/chat/enviar
    public void enviarMensagem(MensagemDTO dto) {
        MensagemDTO salva = mensagemService.salvarMensagem(dto);

        // Envia para todos que estão escutando esse chat específico
        messagingTemplate.convertAndSend("/topic/chat/" + dto.getChatId(), salva);
    }
}


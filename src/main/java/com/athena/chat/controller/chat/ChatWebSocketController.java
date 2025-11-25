package com.athena.chat.controller.chat;


import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.dto.chat.SimpleMensagemDTO;
import com.athena.chat.dto.mapper.MensagemMapper;
import com.athena.chat.model.chat.Mensagem;
import com.athena.chat.services.chat.ChatService;
import com.athena.chat.services.chat.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chats/{chatId}/send")
    @SendTo("/topic/chats/{chatId}")
    public MensagemDTO enviarMensagem(
            @DestinationVariable Long chatId,
            MensagemDTO mensagemDTO,
            Principal principal) {

        try {
            String nomeUsuario;

            if (principal != null) {
                nomeUsuario = principal.getName();
                System.out.println("✅ Principal recebido via WebSocket: " + nomeUsuario);
            } else if (mensagemDTO.getRemetenteNome() != null && !mensagemDTO.getRemetenteNome().isEmpty()) {
                nomeUsuario = mensagemDTO.getRemetenteNome();
                System.out.println("⚠️ Usando fallback remetenteNome: " + nomeUsuario);
            } else {
                System.err.println("❌ Nenhuma informação de usuário disponível");
                throw new IllegalArgumentException("Usuário não identificado");
            }

            System.out.println("📨 Salvando mensagem para chat: " + chatId + " de: " + nomeUsuario);
            MensagemDTO mensagemSalva = chatService.salvarMensagem(chatId, mensagemDTO, nomeUsuario);
            System.out.println("✅ Mensagem salva com sucesso: " + mensagemSalva.getId());

            return mensagemSalva;

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erro na validação: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

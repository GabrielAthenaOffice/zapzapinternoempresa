package com.athena.chat.controller.chat;


import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.dto.chat.SimpleMensagemDTO;
import com.athena.chat.dto.mapper.MensagemMapper;
import com.athena.chat.model.chat.Mensagem;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
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
    private final UserRepository userRepository;

    @MessageMapping("/chats/{chatId}/send")
    @SendTo("/topic/chats/{chatId}")
    public MensagemDTO enviarMensagem(
            @DestinationVariable Long chatId,
            MensagemDTO mensagemDTO,
            Principal principal) {

        try {
            String nomeUsuario;

            if (principal != null) {
                // principal.getName() retorna o email (username)
                String emailUsuario = principal.getName();
                System.out.println("✅ Email do usuário recebido: " + emailUsuario);

                // Buscar o usuário por email para obter o NOME REAL
                User usuarioEncontrado = userRepository.findByEmail(emailUsuario)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

                nomeUsuario = usuarioEncontrado.getNome(); // ✅ Nome real
                System.out.println("✅ Nome do usuário: " + nomeUsuario);

            } else if (mensagemDTO.getRemetenteNome() != null && !mensagemDTO.getRemetenteNome().isEmpty()) {
                nomeUsuario = mensagemDTO.getRemetenteNome();
                System.out.println("⚠️ Usando fallback remetenteNome: " + nomeUsuario);
            } else {
                throw new IllegalArgumentException("Usuário não identificado");
            }

            System.out.println("📨 Salvando mensagem para chat: " + chatId + " de: " + nomeUsuario);
            MensagemDTO mensagemSalva = chatService.salvarMensagem(chatId, mensagemDTO, nomeUsuario);
            System.out.println("✅ Mensagem salva com sucesso: " + mensagemSalva.getId());

            return mensagemSalva;

        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

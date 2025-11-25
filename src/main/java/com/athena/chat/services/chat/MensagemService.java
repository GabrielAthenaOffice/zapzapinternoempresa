package com.athena.chat.services.chat;

import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.dto.mapper.MensagemMapper;
import com.athena.chat.model.chat.Chat;
import com.athena.chat.model.chat.Mensagem;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.repositories.chat.ChatRepository;
import com.athena.chat.repositories.chat.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public MensagemDTO salvarMensagem(Long chatId, MensagemDTO dto, String nomeUsuario) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado"));

        User remetente = userRepository.findByNome(nomeUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não autenticado"));

        // ADICIONAR VALIDAÇÃO
        if (!chat.getParticipantes().contains(remetente)) {
            try {
                throw new IllegalAccessException("Usuário não é participante deste chat");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        dto.setRemetenteId(remetente.getId());
        Mensagem novaMensagem = MensagemMapper.fromDTO(dto, remetente, chat);
        Mensagem salva = mensagemRepository.save(novaMensagem);

        return MensagemMapper.toDTO(salva, remetente.getId());
    }

    public List<MensagemDTO> listarMensagens(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        return chat.getMensagens().stream()
                .map(m -> MensagemMapper.toDTO(m, userId))
                .collect(Collectors.toList());
    }


    public void marcarComoLida(Long mensagemId, Long usuarioId) {
        Mensagem mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        mensagem.getUsuariosQueLeram().add(usuario);
        mensagemRepository.save(mensagem);
    }



}


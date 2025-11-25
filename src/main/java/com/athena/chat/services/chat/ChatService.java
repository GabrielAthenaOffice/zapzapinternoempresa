package com.athena.chat.services.chat;
import com.athena.chat.dto.chat.ChatDTO;
import com.athena.chat.dto.chat.MensagemDTO;
import com.athena.chat.dto.mapper.ChatMapper;
import com.athena.chat.dto.mapper.MensagemMapper;
import com.athena.chat.model.chat.Chat;
import com.athena.chat.model.chat.Mensagem;
import com.athena.chat.model.entities.User;
import com.athena.chat.model.entities.permissions.TipoChat;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.repositories.chat.ChatRepository;
import com.athena.chat.repositories.chat.MensagemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MensagemRepository mensagemRepository;

    public ChatDTO buscarPorId(Long id) {
        Chat chatId = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado com ID: " + id));

        return ChatMapper.toDTO(chatId);
    }

    public List<ChatDTO> listarTodos() {

        List<Chat> chatList = chatRepository.findAll();

        if(chatList.isEmpty()) {

        }

        List<ChatDTO> dtos = chatList.stream()
                .map(ChatMapper::toDTO)
                .toList();

        return dtos;
    }


    public Chat criarChat(String nome) {
        Chat chat = new Chat();
        chat.setNome(nome);

       /* Set<User> participantes = idsParticipantes.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id)))
                .collect(Collectors.toSet()); */

        Chat chatSalvo = chatRepository.save(chat);

        return chatSalvo;
    }

    public List<ChatDTO> listarChatsDoUsuario(Long userId) {
        List<Chat> chatsLinkados = chatRepository.findByParticipantes_Id(userId);

        if (chatsLinkados.isEmpty()) {
            return List.of();
        }

        return chatsLinkados.stream()
                .map(ChatMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void adicionarParticipante(Chat chat, User user) {
        chat.getParticipantes().add(user);
        chatRepository.save(chat);
    }

    public void removerParticipante(Chat chat, User user) {
        chat.getParticipantes().remove(user);
        chatRepository.save(chat);
    }

    @Transactional
    public MensagemDTO salvarMensagem(Long chatId, MensagemDTO dto, String emailUsuario) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado"));

        User remetente = userRepository.findByNome(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não autenticado"));

        Mensagem novaMensagem = MensagemMapper.fromDTO(dto, remetente, chat);

        Mensagem salva = mensagemRepository.save(novaMensagem);

        return MensagemMapper.toDTO(salva, remetente.getId());
    }

    @Transactional
    public ChatDTO criarChatPrivado(Long usuario1Id, Long usuario2Id) {
        // Verificar se já existe chat entre esses usuários
        Optional<Chat> chatExistente = chatRepository
                .findChatPrivadoEntreUsuarios(usuario1Id, usuario2Id);

        if (chatExistente.isPresent()) {
            return ChatMapper.toDTO(chatExistente.get());
        }

        User user1 = userRepository.findById(usuario1Id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário 1 não encontrado"));
        User user2 = userRepository.findById(usuario2Id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário 2 não encontrado"));

        Chat chat = new Chat();
        chat.setTipo(TipoChat.PRIVADO);
        chat.setNome(user1.getNome() + " - " + user2.getNome());
        chat.getParticipantes().add(user1);
        chat.getParticipantes().add(user2);

        Chat salvo = chatRepository.save(chat);
        return ChatMapper.toDTO(salvo);
    }
}

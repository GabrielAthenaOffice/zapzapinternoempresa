package com.athena.chat.services;

import com.athena.chat.dto.GroupCreateDTO;
import com.athena.chat.dto.GroupDTO;
import com.athena.chat.dto.mapper.GroupMapper;
import com.athena.chat.model.chat.Chat;
import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;
import com.athena.chat.model.entities.permissions.TipoChat;
import com.athena.chat.repositories.GroupRepository;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.repositories.chat.ChatRepository;
import com.athena.chat.services.chat.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public List<GroupDTO> listarGrupos() throws IllegalAccessException {
        List<Group> grupos = groupRepository.findAll();

        if (grupos.isEmpty()) {
            throw new IllegalAccessException("Nenhum grupo criado até o momento");
        }

        return grupos.stream()
                .map(GroupMapper::toDTO)
                .toList();
    }

    public Optional<Stream<GroupDTO>> buscarPorId(Long id) {
        Optional<Group> group = Optional.ofNullable(groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum grupo encontrado")));

        return Optional.ofNullable((group.stream()
                .map(GroupMapper::toDTO)));
    }

    public List<GroupDTO> buscarGruposPorCriador() {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        User usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        List<Group> grupos = groupRepository.findByCriadoPor(usuario);

        return grupos.stream()
                .map(GroupMapper::toDTO)
                .toList();
    }

    @Transactional
    public GroupDTO criarGrupo(GroupCreateDTO dto, User autenticado) {

        // Garante que o criador eh um usuario
        User criador = userRepository.findById(autenticado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));

        // Conjunto de IDs: usuarios do DTO + criador
        Set<Long> ids = new HashSet<>(dto.getUsuariosIds() != null ? dto.getUsuariosIds() : List.of());
        ids.add(criador.getId());

        // Carrega todos de uma vez
        List<User> usuarios = userRepository.findAllById(ids);

        if (usuarios.size() != ids.size()) {
            throw new IllegalArgumentException("Um ou mais usuários não foram encontrados.");
        }

        // MONTA O GRUPO
        Group grupo = new Group();
        grupo.setNome(dto.getNome());
        grupo.setDescricao(dto.getDescricao());
        grupo.setCriadoPor(criador);

        for (User u : usuarios) {
            grupo.getMembros().add(u);
            // eh opcional, mas deixei pra aprender como trata essas relacoes
            u.getGrupos().add(grupo);
        }

        // MONTA O CHAT
        Chat chat = new Chat();
        chat.setNome(dto.getNome());
        chat.setTipo(TipoChat.GRUPO);

        for (User u : usuarios) {
            chat.getParticipantes().add(u);
        }

        // LIGA OS DOIS LADOS
        chat.setGrupo(grupo);
        grupo.setChat(chat);

        // SALVA SO O CHAT (dono do OneToOne, cascade = ALL)
        Chat chatSalvo = chatRepository.save(chat);
        Group grupoSalvo = chatSalvo.getGrupo();

        log.info("Grupo e chat criados: Grupo={}, Chat={}, Criador={}, Membros={}",
                grupoSalvo.getNome(), chatSalvo.getNome(), criador.getNome(), ids);

        GroupDTO groupDTO = GroupMapper.toDTO(grupoSalvo);
        groupDTO.setChatId(chatSalvo.getId());

        return groupDTO;
    }


    public GroupDTO atualizarGrupo(Long id, GroupDTO groupDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado com id: " + id));

        group.setNome(groupDTO.getNome());
        group.setDescricao(groupDTO.getDescricao());

        Group atualizado = groupRepository.save(group);
        return GroupMapper.toDTO(atualizado);
    }


    @Transactional
    public GroupDTO deletarGrupo(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        groupRepository.delete(group);

        log.warn("Grupo deletado: ID={}, Nome={}", group.getId(), group.getNome());

        return GroupMapper.toDTO(group);
    }

    @Transactional
    public GroupDTO adicionarUsuarioAoGrupo(Long groupId, Long userId) {

        Group grupo = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // caso seja membro vai so retornar o grupo
        if (!grupo.getMembros().contains(usuario)) {
            grupo.getMembros().add(usuario);
            // manter o lado inverso coerente em memória
            usuario.getGrupos().add(grupo);
        }

        // se existir chat vinculado, adiciona também la
        Chat chat = grupo.getChat();
        if (chat != null && !chat.getParticipantes().contains(usuario)) {
            chat.getParticipantes().add(usuario);
        }

        // NAO precisa salvar user separado.
        // Como esta tudo manuseado dentro da transacao, pode:
        // 1) confiar no dirty checking e não chamar save nenhum
        // ou
        // 2) salvar so o "agregado raiz", por exemplo o chat:
        if (chat != null) {
            chatRepository.save(chat); // cascade = ALL para Group
        } else {
            groupRepository.save(grupo);
        }

        log.info("Usuário {} adicionado ao grupo {} e ao chat {}",
                usuario.getNome(),
                grupo.getNome(),
                chat != null ? chat.getNome() : "sem chat");

        return GroupMapper.toDTO(grupo);
    }

    @Transactional
    public GroupDTO removerUsuarioDoGrupo(Long groupId, Long userId) {

        Group grupo = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        User usuario = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!grupo.getMembros().contains(usuario)) {
            throw new IllegalArgumentException("Usuário não faz parte deste grupo");
        }

        // regra opcional: impedir remover o criador
        if (grupo.getCriadoPor() != null &&
                grupo.getCriadoPor().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Não é permitido remover o criador do grupo");
        }

        // remove dos dois lados da ManyToMany
        grupo.getMembros().remove(usuario);
        usuario.getGrupos().remove(grupo);

        // se tiver chat vinculado, remove la também
        Chat chat = grupo.getChat();
        if (chat != null) {
            chat.getParticipantes().remove(usuario);
        }

        // de novo: 1 save so
        if (chat != null) {
            chatRepository.save(chat);
        } else {
            groupRepository.save(grupo);
        }

        log.info("Usuário {} removido do grupo {} e do chat {}",
                usuario.getEmail(),
                grupo.getNome(),
                chat != null ? chat.getNome() : "sem chat");

        return GroupMapper.toDTO(grupo);
    }

}

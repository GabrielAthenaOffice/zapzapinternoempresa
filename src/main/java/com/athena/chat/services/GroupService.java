package com.athena.chat.services;


import com.athena.chat.dto.GroupCreateDTO;
import com.athena.chat.dto.GroupDTO;
import com.athena.chat.dto.mapper.GroupMapper;
import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.GroupRepository;
import com.athena.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<GroupDTO> listarGrupos() throws IllegalAccessException {
        List<Group> grupos = groupRepository.findAll();

        if (grupos.isEmpty()) {
            throw new IllegalAccessException("Nenhum grupo criado até o momento");
        }

        List<GroupDTO> groupDTOS = grupos.stream()
                .map(GroupMapper::toDTO)
                .toList();

        return groupDTOS;
    }

    public Optional<Stream<GroupDTO>> buscarPorId(Long id) {
        Optional<Group> group = Optional.ofNullable(groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nenhum usuário encontrado")));

        Optional<Stream<GroupDTO>> grupo = Optional.ofNullable((group.stream()
                .map(GroupMapper::toDTO)));

        return grupo;
    }

    public Group criarGrupo(GroupCreateDTO dto) {

        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        User criador = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não encontrado."));


        Group grupo = new Group();
        grupo.setNome(dto.getNome());
        grupo.setDescricao(dto.getDescricao());
        grupo.setCriadoPor(criador);

        return groupRepository.save(grupo);
    }

    public GroupDTO atualizarGrupo(Long id, GroupDTO groupDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado com id: " + id));

        group.setNome(groupDTO.getNome());
        group.setDescricao(groupDTO.getDescricao());

        Group atualizado = groupRepository.save(group);
        return GroupMapper.toDTO(atualizado);
    }


    public GroupDTO deletarGrupo(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        groupRepository.delete(group);

        return GroupMapper.toDTO(group);
    }

    public GroupDTO adicionarUsuarioAoGrupo(Long groupId, Long userId) throws IllegalAccessException {
        try {
            Group grupo = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

            User usuario = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            grupo.getMembros().add(usuario);
            Group grupoAtualizado = groupRepository.save(grupo);

            return GroupMapper.toDTO(grupoAtualizado);

        } catch (IllegalArgumentException e) {
            throw new IllegalAccessException("Não foi possível adicionar o usuário: " + e.getMessage());
        }
    }

    public GroupDTO removerUsuarioDoGrupo(Long groupId, Long userId) throws IllegalAccessException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo não encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!group.getMembros().contains(user)) {
            throw new IllegalAccessException("Usuário não faz parte deste grupo");
        }

        group.getMembros().remove(user);
        groupRepository.save(group);

        return GroupMapper.toDTO(group);
    }

}

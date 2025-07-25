package com.athena.chat.services;


import com.athena.chat.dto.GroupCreateDTO;
import com.athena.chat.dto.simpledto.GroupSimpleDTO;
import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.GroupRepository;
import com.athena.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<Group> listarGrupos() {
        return groupRepository.findAll();
    }

    public Optional<Group> buscarPorId(Long id) {
        return groupRepository.findById(id);
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

    public Optional<Group> atualizarGrupo(Long id, GroupSimpleDTO dto) {
        return groupRepository.findById(id).map(group -> {
            if (dto.getNome() != null) group.setNome(dto.getNome());
            if (dto.getDescricao() != null) group.setDescricao(dto.getDescricao());
            return groupRepository.save(group);
        });
    }

    public void deletar(Long id) {
        groupRepository.deleteById(id);
    }

    public Optional<Group> adicionarUsuarioAoGrupo(Long groupId, Long userId) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (groupOpt.isPresent() && userOpt.isPresent()) {
            Group group = groupOpt.get();
            group.getMembros().add(userOpt.get());
            groupRepository.save(group);
            return Optional.of(group);
        }
        return Optional.empty();
    }
}

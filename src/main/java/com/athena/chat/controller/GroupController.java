package com.athena.chat.controller;

import com.athena.chat.dto.GroupCreateDTO;
import com.athena.chat.dto.GroupDTO;
import com.athena.chat.dto.mapper.GroupMapper;
import com.athena.chat.dto.mapper.UserMapper;
import com.athena.chat.dto.simpledto.UserSimpleDTO;
import com.athena.chat.model.entities.Group;
import com.athena.chat.services.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public List<GroupDTO> listarGrupos() {
        return groupService.listarGrupos()
                .stream()
                .map(GroupMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<List<UserSimpleDTO>> listarUsuariosPorGrupo(@PathVariable Long id) {
        return groupService.buscarPorId(id)
                .map(group -> {
                    List<UserSimpleDTO> usuarios = group.getMembros()
                            .stream()
                            .map(UserMapper::toSimpleDTO)
                            .toList();
                    return ResponseEntity.ok(usuarios);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> buscarPorId(@PathVariable Long id) {
        return groupService.buscarPorId(id)
                .map(GroupMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public GroupDTO criarGrupo(@RequestBody @Valid GroupCreateDTO dto) {
        Group group = new Group();
        group.setNome(dto.getNome());
        group.setDescricao(dto.getDescricao());

        // aqui poderíamos buscar o User pelo ID dto.getCriadoPor()
        return GroupMapper.toDTO(groupService.salvar(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> atualizarGrupo(@PathVariable Long id, @RequestBody Group group) {
        return groupService.atualizar(id, group)
                .map(GroupMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{groupId}/usuarios/{userId}")
    public ResponseEntity<GroupDTO> adicionarUsuario(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.adicionarUsuarioAoGrupo(groupId, userId)
                .map(GroupMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.athena.chat.controller;

import com.athena.chat.dto.UserCreateDTO;
import com.athena.chat.dto.UserDTO;
import com.athena.chat.dto.mapper.UserMapper;
import com.athena.chat.model.entities.User;
import com.athena.chat.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> listarUsuarios() {
        return userService.listarUsuarios()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable Long id) {
        return userService.buscarPorId(id)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserDTO criarUsuario(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        User user = UserMapper.toEntity(userCreateDTO);
        return UserMapper.toDTO(userService.salvar(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UserCreateDTO userCreateDTO
    ) {
        User novo = UserMapper.toEntity(userCreateDTO);
        return userService.atualizar(id, novo)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        userService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

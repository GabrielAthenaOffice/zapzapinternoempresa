package com.athena.chat.services;

import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    public User salvar(User user) {
        return userRepository.save(user);
    }

    public Optional<User> atualizar(Long id, User userAtualizado) {
        return userRepository.findById(id).map(user -> {
            user.setNome(userAtualizado.getNome());
            user.setEmail(userAtualizado.getEmail());
            user.setSenha(userAtualizado.getSenha());
            user.setCargo(userAtualizado.getCargo());
            user.setRole(userAtualizado.getRole());
            return userRepository.save(user);
        });
    }

    public void deletar(Long id) {
        userRepository.deleteById(id);
    }
}

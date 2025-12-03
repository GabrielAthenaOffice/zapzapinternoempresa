package com.athena.chat.services;

import com.athena.chat.config.security.LoginResponseDTO;
import com.athena.chat.config.security.TokenService;
import com.athena.chat.dto.UserCreateDTO;
import com.athena.chat.dto.UserDTO;
import com.athena.chat.dto.mapper.UserMapper;
import com.athena.chat.dto.simpledto.AuthenticationDTO;
import com.athena.chat.dto.simpledto.UserSimpleDTO;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
import com.athena.chat.services.storage.SupabaseStorageService;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final SupabaseStorageService supabaseStorageService;

    public LoginResponseDTO login(AuthenticationDTO data) {
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.email(), data.senha()));
        } catch (AuthenticationException exception) {
            throw new RuntimeException("Bad credentials", exception);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();
        ResponseCookie jwtCookie = tokenService.generateCookie(userDetails);

        UserSimpleDTO userSimpleDTO = new UserSimpleDTO(userDetails.getId(), userDetails.getNome(),
                userDetails.getEmail(), userDetails.getRole(), userDetails.getFotoPerfil());

        return new LoginResponseDTO(userSimpleDTO, jwtCookie.toString());
    }

    public User registrar(UserCreateDTO data) {
        if (this.userRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já está em uso.");
        }

        User registro = UserMapper.toEntity(data);

        String encryptedPassword = new BCryptPasswordEncoder().encode(registro.getSenha());
        User user = new User(data.getNome(), data.getEmail(), encryptedPassword,
                data.getCargo(), data.getRole());

        return this.userRepository.save(user);
    }

    public UserDTO atualizarUsuario(Long id, UserDTO userDTO) {
        User savedUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (userDTO.getNome() != null && !userDTO.getNome().isBlank()) {
            savedUser.setNome(userDTO.getNome());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()) {
            // Check if email is already taken by another user
            userRepository.findByEmail(userDTO.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(id)) {
                            throw new IllegalArgumentException("E-mail já está em uso.");
                        }
                    });
            savedUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getSenha() != null && !userDTO.getSenha().isBlank()) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.getSenha());
            savedUser.setSenha(encryptedPassword);
        }
        if (userDTO.getCargo() != null) {
            savedUser.setCargo(userDTO.getCargo());
        }
        if (userDTO.getFotoPerfil() != null) {
            savedUser.setFotoPerfil(userDTO.getFotoPerfil());
        }
        if (userDTO.getRole() != null) {
            savedUser.setRole(userDTO.getRole());
        }

        savedUser = userRepository.save(savedUser);

        return UserMapper.toDTO(savedUser);
    }

    public String uploadProfilePhoto(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String filePath = supabaseStorageService.uploadFile(file, "avatars");
        String publicUrl = supabaseStorageService.getPublicUrl(filePath);

        user.setFotoPerfil(publicUrl);
        userRepository.save(user);

        return publicUrl;
    }

    public UserDTO deletarUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        userRepository.delete(user);

        return UserMapper.toDTO(user);
    }

}

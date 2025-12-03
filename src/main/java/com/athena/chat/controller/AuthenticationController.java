package com.athena.chat.controller;

import com.athena.chat.config.exceptions.MessageResponse;
import com.athena.chat.config.security.LoginResponseDTO;
import com.athena.chat.config.security.TokenService;
import com.athena.chat.dto.UserCreateDTO;
import com.athena.chat.dto.UserDTO;
import com.athena.chat.dto.simpledto.AuthenticationDTO;
import com.athena.chat.dto.simpledto.UserSimpleDTO;
import com.athena.chat.model.entities.User;
import com.athena.chat.services.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final LoginService loginService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            LoginResponseDTO response = loginService.login(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, response.cookie())
                    .body(response.userDTO());

        } catch (RuntimeException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);

            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("register")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIDER_DE_SETOR')")
    public ResponseEntity<User> register(@RequestBody @Valid UserCreateDTO data) {
        User registrar = loginService.registrar(data);

        return new ResponseEntity<>(registrar, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> atualizarUsuario(@PathVariable Long id,
            @RequestBody @Valid UserDTO userDTO) {
        UserDTO user = loginService.atualizarUsuario(id, userDTO);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/{id}/photo")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Map<String, String>> uploadPhoto(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String photoUrl = loginService.uploadProfilePhoto(id, file);
        Map<String, String> response = new HashMap<>();
        response.put("url", photoUrl);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> deletarUsuario(@PathVariable Long id) {
        UserDTO deletarUsuario = loginService.deletarUsuario(id);

        return new ResponseEntity<>(deletarUsuario, HttpStatus.OK);
    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if (authentication != null) {
            return authentication.getName();
        } else {
            return "NULL";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserSimpleDTO> getUserDetails(Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();

        UserSimpleDTO userSimpleDTO = new UserSimpleDTO(userDetails.getId(),
                userDetails.getNome(), userDetails.getEmail(), userDetails.getRole(), userDetails.getFotoPerfil());

        return ResponseEntity.ok().body(userSimpleDTO);
    }

    @PostMapping("/singout")
    public ResponseEntity<?> logoutApp() {
        ResponseCookie cookie = tokenService.getCleanCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You have been signed out"));
    }

}

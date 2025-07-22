package com.athena.chat.controller;

import com.athena.chat.dto.simpledto.AuthenticationDTO;
import com.athena.chat.dto.simpledto.RegisterDTO;
import com.athena.chat.model.entities.User;
import com.athena.chat.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var login = new UsernamePasswordAuthenticationToken(data.nome(), data.senha());
        var auth = authenticationManager.authenticate(login);

        return ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if(this.userRepository.findByEmail(data.nome()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        User user = new User(data.nome(), data.email(), encryptedPassword,
                data.cargo(), data.roles());

        this.userRepository.save(user);

        return ResponseEntity.ok().build();

    }

}

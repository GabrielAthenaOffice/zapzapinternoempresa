package com.athena.chat.config;

import com.athena.chat.model.entities.Group;
import com.athena.chat.model.entities.User;
import com.athena.chat.model.entities.permissions.UserRole;
import com.athena.chat.repositories.GroupRepository;
import com.athena.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findByEmail("principal@athena.com").isEmpty()) {
                User admin = new User();
                admin.setNome("Diretor");
                admin.setEmail("principal@athena.com");
                admin.setSenha(passwordEncoder.encode("Athena2025@"));
                admin.setRole(UserRole.ADMIN);

                userRepository.save(admin);
            }
        };
    }


}

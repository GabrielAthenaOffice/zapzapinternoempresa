package com.athena.chat.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String cargo;
    private String role; // admin, user, etc.
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToMany(mappedBy = "membros")
    private Set<Group> grupos = new HashSet<>();
}


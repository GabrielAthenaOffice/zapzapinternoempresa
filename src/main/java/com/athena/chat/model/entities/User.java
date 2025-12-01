package com.athena.chat.model.entities;

import com.athena.chat.model.entities.permissions.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "usuarios")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String cargo;

    @Enumerated(EnumType.STRING)
    private UserRole role; // ADMIN, FUNCIONARIO ou ESTAGIARIO
    private LocalDateTime criadoEm = LocalDateTime.now();

    @ManyToMany(mappedBy = "membros")
    @JsonIgnore
    private Set<Group> grupos = new HashSet<>();

    public User(String nome, String email, String senha, String cargo, UserRole userRole) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
        this.role = userRole;
        this.criadoEm = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Adiciona a role principal (ex: ROLE_ADMIN)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));

        // Adiciona todas as permissões associadas à role (ex: PERMISSION_USER_CREATE)
        this.role.getPermissions()
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.name())));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

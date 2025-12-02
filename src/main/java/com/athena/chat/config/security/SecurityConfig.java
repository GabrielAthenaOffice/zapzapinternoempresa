package com.athena.chat.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 🔑 Mudar para IF_REQUIRED para permitir cookies em requests autenticados
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(
                        authorize -> authorize
                                // SWAGGER
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        "/swagger-resources/**",
                                        "/configuration/security",
                                        "/configuration/ui")
                                .permitAll()
                                // Liberar login
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                // Registro apenas para ADMIN e LIDER_DE_SETOR
                                .requestMatchers(HttpMethod.POST, "/auth/register")
                                .hasAnyRole("ADMIN", "LIDER_DE_SETOR")
                                .requestMatchers(HttpMethod.GET, "/auth/user").authenticated() // Agora autenticado mas
                                                                                               // permitido
                                .requestMatchers(HttpMethod.POST, "/auth/singout").authenticated() // Logout
                                .requestMatchers("/ws/**").permitAll() // libera websocket + sockJS
                                .requestMatchers("/app/**").permitAll()

                                // Regras para API - controle granular via @PreAuthorize nos controllers
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração consolidada de CORS com suporte a credenciais
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("https://frontzapzapinterno.vercel.app/")); // Específico para desenvolvimento
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos explícitos
        configuration.setAllowedHeaders(List.of("*")); // Headers permitidos
        configuration.setAllowCredentials(true); // Permite cookies/credenciais
        configuration.setMaxAge(3600L); // Cache de 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

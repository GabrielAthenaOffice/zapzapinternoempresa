package com.athena.chat.config.security;

import com.athena.chat.model.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.cookies.secrets}")
    private String jwtCookie;

    @Value("${api.security.cookie.secure}")
    private boolean cookieSecure;

    @Value("${api.security.cookie.samesite}")
    private String cookieSameSite;

    // No TokenService.java do Chat
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    // Adicionar estes claims obrigatorios para o Fluxo
                    .withClaim("role", user.getRole().toString()) // Enum para String
                    .withClaim("userId", user.getId())
                    .withClaim("name", user.getNome())
                    // fim das adicoes
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro para gerar o token", exception);
        }
    }

    public ResponseCookie generateCookie(User userPrincipal) {
        String jwt = generateToken(userPrincipal);

        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/") // ✅ Disponível para todas as rotas
                .maxAge(24 * 60 * 60)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .build();

        return cookie;
    }

    public String getJwtFromCookies(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, jwtCookie);

        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }

    }

    public ResponseCookie getCleanCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .build();

        return cookie;
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null; // token inválido ou expirado
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

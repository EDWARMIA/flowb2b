package com.flowb2b.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generarToken(
            String correo,
            Long idUsuario,
            Long empresaId,
            Set<String> roles,
            Set<String> permisos) {

        Map<String, Object> claims = Map.of(
                "idUsuario", idUsuario,
                "empresaId", empresaId,
                "roles", roles,
                "permisos", permisos
        );

        Date ahora = new Date();

        Date vencimiento = new Date(
                ahora.getTime() + expiration
        );

        return Jwts.builder()
                .claims(claims)
                .subject(correo)
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(getSigningKey())
                .compact();
    }

    public String obtenerCorreo(String token) {

        return obtenerClaims(token)
                .getSubject();
    }

    public Long obtenerEmpresaId(String token) {

        Number empresaId = obtenerClaims(token)
                .get("empresaId", Number.class);

        return empresaId.longValue();
    }

    public Long obtenerIdUsuario(String token) {

        Number idUsuario = obtenerClaims(token)
                .get("idUsuario", Number.class);

        return idUsuario.longValue();
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerRoles(String token) {

        return obtenerClaims(token)
                .get("roles", List.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerPermisos(String token) {

        return obtenerClaims(token)
                .get("permisos", List.class);
    }

    public boolean tokenValido(String token) {

        try {

            obtenerClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    private Claims obtenerClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
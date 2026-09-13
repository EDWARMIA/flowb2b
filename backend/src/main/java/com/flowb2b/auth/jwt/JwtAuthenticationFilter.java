package com.flowb2b.auth.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flowb2b.auth.security.UsuarioAutenticado;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader.substring(7);

        if (!jwtService.tokenValido(token)) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    """
                    {
                      "estado": 401,
                      "error": "Token inválido o vencido"
                    }
                    """
            );

            return;
        }

        String correo =
                jwtService.obtenerCorreo(token);

        Long idUsuario =
                jwtService.obtenerIdUsuario(token);

        Long empresaId =
                jwtService.obtenerEmpresaId(token);

        List<String> roles =
                jwtService.obtenerRoles(token);

        List<String> permisos =
                jwtService.obtenerPermisos(token);

        List<SimpleGrantedAuthority> authorities =
                new ArrayList<>();

        // =========================
        // ROLES
        // =========================

        for (String rol : roles) {

            authorities.add(
                new SimpleGrantedAuthority(
                    "ROLE_" + rol
                )
            );
        }

        // =========================
        // PERMISOS
        // =========================

        for (String permiso : permisos) {

            authorities.add(
                new SimpleGrantedAuthority(
                    permiso
                )
            );
        }

        // =========================
        // USUARIO AUTENTICADO
        // =========================

        UsuarioAutenticado usuarioAutenticado =
                new UsuarioAutenticado(
                        idUsuario,
                        empresaId,
                        correo
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null,
                        authorities
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );

        filterChain.doFilter(
                request,
                response
        );
    }
}
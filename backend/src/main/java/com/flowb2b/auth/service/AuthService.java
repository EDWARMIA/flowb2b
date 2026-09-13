package com.flowb2b.auth.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.dto.LoginRequestDTO;
import com.flowb2b.auth.dto.LoginResponseDTO;
import com.flowb2b.auth.jwt.JwtService;
import com.flowb2b.seguridad.entity.Permiso;
import com.flowb2b.seguridad.entity.Rol;
import com.flowb2b.usuario.entity.Usuario;
import com.flowb2b.usuario.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Usuario usuario = usuarioRepository
                .findByCorreo(dto.getCorreo())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Correo o contraseña incorrectos"
                    )
                );

        if (!Boolean.TRUE.equals(usuario.getEstado())) {

            throw new IllegalArgumentException(
                "El usuario se encuentra inactivo"
            );
        }

        boolean passwordCorrecta =
                passwordEncoder.matches(
                    dto.getPassword(),
                    usuario.getPasswordHash()
                );

        if (!passwordCorrecta) {

            throw new IllegalArgumentException(
                "Correo o contraseña incorrectos"
            );
        }

        Set<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        Set<String> permisos = usuario.getRoles()
                .stream()
                .flatMap(
                    rol -> rol.getPermisos().stream()
                )
                .map(Permiso::getCodigo)
                .collect(Collectors.toSet());

        String token = jwtService.generarToken(
                usuario.getCorreo(),
                usuario.getIdUsuario(),
                usuario.getEmpresaId(),
                roles,
                permisos
        );

        LoginResponseDTO response =
                new LoginResponseDTO();

        response.setIdUsuario(
                usuario.getIdUsuario()
        );

        response.setEmpresaId(
                usuario.getEmpresaId()
        );

        response.setNombre(
                usuario.getNombre()
                + " "
                + usuario.getApellido()
        );

        response.setCorreo(
                usuario.getCorreo()
        );

        response.setRoles(
                roles
        );

        response.setPermisos(
                permisos
        );

        response.setMensaje(
                "Inicio de sesión correcto"
        );

        response.setToken(
                token
        );

        return response;
    }
}
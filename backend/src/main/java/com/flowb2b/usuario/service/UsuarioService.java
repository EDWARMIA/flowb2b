package com.flowb2b.usuario.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.seguridad.entity.Rol;
import com.flowb2b.seguridad.repository.RolRepository;
import com.flowb2b.usuario.dto.UsuarioRequestDTO;
import com.flowb2b.usuario.dto.UsuarioResponseDTO;
import com.flowb2b.usuario.entity.Usuario;
import com.flowb2b.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuarios() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + id
                    )
                );

        return convertirResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        if (usuarioRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un usuario con el correo: " + dto.getCorreo()
            );
        }

        Rol rol = rolRepository.findById(dto.getRolId())
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Rol no encontrado con ID: " + dto.getRolId()
                    )
                );

        if (!rol.getEmpresaId().equals(dto.getEmpresaId())) {
            throw new IllegalArgumentException(
                "El rol seleccionado no pertenece a la empresa indicada"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setEmpresaId(dto.getEmpresaId());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());

        usuario.setPasswordHash(
            passwordEncoder.encode(dto.getPassword())
        );

        usuario.setEstado(true);

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);

        usuario.setRoles(roles);

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        return convertirResponseDTO(usuarioGuardado);
    }

    private UsuarioResponseDTO convertirResponseDTO(Usuario usuario) {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setEmpresaId(usuario.getEmpresaId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setEstado(usuario.getEstado());

        Set<String> roles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());

        dto.setRoles(roles);

        return dto;
    }
}
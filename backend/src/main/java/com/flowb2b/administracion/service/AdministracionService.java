package com.flowb2b.administracion.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.administracion.dto.ActualizarUsuarioAdministracionDTO;
import com.flowb2b.administracion.dto.AdministracionPermisoDTO;
import com.flowb2b.administracion.dto.AdministracionRolDTO;
import com.flowb2b.administracion.dto.AdministracionUsuarioDTO;
import com.flowb2b.administracion.dto.CrearUsuarioAdministracionDTO;
import com.flowb2b.administracion.dto.RolAdministracionRequestDTO;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.seguridad.entity.Permiso;
import com.flowb2b.seguridad.entity.Rol;
import com.flowb2b.seguridad.repository.PermisoRepository;
import com.flowb2b.seguridad.repository.RolRepository;
import com.flowb2b.usuario.entity.Usuario;
import com.flowb2b.usuario.repository.UsuarioRepository;

@Service
public class AdministracionService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministracionService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PermisoRepository permisoRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // USUARIO AUTENTICADO
    // =====================================================

    private UsuarioAutenticado obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalArgumentException(
                    "No existe un usuario autenticado"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioAutenticado)) {

            throw new IllegalArgumentException(
                    "Principal autenticado inválido"
            );
        }

        return (UsuarioAutenticado) principal;
    }

    private Long obtenerEmpresaId() {

        return obtenerUsuarioAutenticado()
                .getEmpresaId();
    }

    // =====================================================
    // USUARIOS
    // =====================================================

    @Transactional(readOnly = true)
    public List<AdministracionUsuarioDTO> listarUsuarios() {

        Long empresaId =
                obtenerEmpresaId();

        return usuarioRepository
                .findByEmpresaIdOrderByNombreAscApellidoAsc(
                        empresaId
                )
                .stream()
                .map(this::convertirUsuarioDTO)
                .toList();
    }

    // =====================================================
    // CREAR USUARIO
    // =====================================================

    @Transactional
    public AdministracionUsuarioDTO crearUsuario(
            CrearUsuarioAdministracionDTO dto) {

        Long empresaId =
                obtenerEmpresaId();

        // ================================================
        // VALIDAR NOMBRE
        // ================================================

        if (dto.getNombre() == null ||
                dto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        // ================================================
        // VALIDAR APELLIDO
        // ================================================

        if (dto.getApellido() == null ||
                dto.getApellido().isBlank()) {

            throw new IllegalArgumentException(
                    "El apellido es obligatorio"
            );
        }

        // ================================================
        // VALIDAR CORREO
        // ================================================

        if (dto.getCorreo() == null ||
                dto.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio"
            );
        }

        String correo =
                dto.getCorreo()
                        .trim()
                        .toLowerCase();

        if (!correo.contains("@")) {

            throw new IllegalArgumentException(
                    "El correo no tiene un formato válido"
            );
        }

        if (usuarioRepository
                .findByCorreo(correo)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo"
            );
        }

        // ================================================
        // VALIDAR CONTRASEÑA
        // ================================================

        if (dto.getPassword() == null ||
                dto.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }

        if (dto.getPassword().length() < 8) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        // ================================================
        // VALIDAR ROLES
        // ================================================

        if (dto.getRolesIds() == null ||
                dto.getRolesIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "El usuario debe tener al menos un rol"
            );
        }

        Set<Rol> roles =
                obtenerRolesEmpresa(
                        dto.getRolesIds(),
                        empresaId
                );

        // ================================================
        // CREAR USUARIO
        // ================================================

        Usuario usuario =
                new Usuario();

        usuario.setEmpresaId(
                empresaId
        );

        usuario.setNombre(
                dto.getNombre().trim()
        );

        usuario.setApellido(
                dto.getApellido().trim()
        );

        usuario.setCorreo(
                correo
        );

        usuario.setPasswordHash(
                passwordEncoder.encode(
                        dto.getPassword()
                )
        );

        usuario.setEstado(
                dto.getEstado() != null
                        ? dto.getEstado()
                        : true
        );

        usuario.setRoles(
                roles
        );

        Usuario usuarioGuardado =
                usuarioRepository.save(
                        usuario
                );

        return convertirUsuarioDTO(
                usuarioGuardado
        );
    }

    // =====================================================
    // ACTUALIZAR USUARIO
    // =====================================================

    @Transactional
    public AdministracionUsuarioDTO actualizarUsuario(
            Long idUsuario,
            ActualizarUsuarioAdministracionDTO dto) {

        Long empresaId =
                obtenerEmpresaId();

        Usuario usuario =
                usuarioRepository
                        .findByIdUsuarioAndEmpresaId(
                                idUsuario,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Usuario no encontrado"
                                )
                        );

        if (dto.getEstado() != null) {

            usuario.setEstado(
                    dto.getEstado()
            );
        }

        if (dto.getRolesIds() != null) {

            if (dto.getRolesIds().isEmpty()) {

                throw new IllegalArgumentException(
                        "El usuario debe tener al menos un rol"
                );
            }

            Set<Rol> roles =
                    obtenerRolesEmpresa(
                            dto.getRolesIds(),
                            empresaId
                    );

            usuario.setRoles(
                    roles
            );
        }

        Usuario usuarioGuardado =
                usuarioRepository.save(
                        usuario
                );

        return convertirUsuarioDTO(
                usuarioGuardado
        );
    }

    // =====================================================
    // OBTENER ROLES DE LA EMPRESA
    // =====================================================

    private Set<Rol> obtenerRolesEmpresa(
            Set<Long> rolesIds,
            Long empresaId) {

        Set<Rol> roles =
                new HashSet<>();

        for (Long rolId : rolesIds) {

            Rol rol =
                    rolRepository
                            .findByIdRolAndEmpresaId(
                                    rolId,
                                    empresaId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El rol "
                                                    + rolId
                                                    + " no pertenece a la empresa"
                                    )
                            );

            roles.add(
                    rol
            );
        }

        return roles;
    }

    // =====================================================
    // ROLES
    // =====================================================

    @Transactional(readOnly = true)
    public List<AdministracionRolDTO> listarRoles() {

        Long empresaId =
                obtenerEmpresaId();

        return rolRepository
                .findByEmpresaIdOrderByNombreAsc(
                        empresaId
                )
                .stream()
                .map(this::convertirRolDTO)
                .toList();
    }

    // =====================================================
    // CREAR ROL
    // =====================================================

    @Transactional
    public AdministracionRolDTO crearRol(
            RolAdministracionRequestDTO dto) {

        Long empresaId =
                obtenerEmpresaId();

        if (dto.getNombre() == null ||
                dto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del rol es obligatorio"
            );
        }

        String nombre =
                dto.getNombre()
                        .trim();

        boolean existe =
                rolRepository
                        .existsByEmpresaIdAndNombreIgnoreCase(
                                empresaId,
                                nombre
                        );

        if (existe) {

            throw new IllegalArgumentException(
                    "Ya existe un rol con ese nombre"
            );
        }

        Rol rol =
                new Rol();

        rol.setEmpresaId(
                empresaId
        );

        rol.setNombre(
                nombre
        );

        rol.setDescripcion(
                dto.getDescripcion()
        );

        rol.setEstado(
                dto.getEstado() != null
                        ? dto.getEstado()
                        : true
        );

        rol.setPermisos(
                obtenerPermisos(
                        dto.getPermisosIds()
                )
        );

        Rol rolGuardado =
                rolRepository.save(
                        rol
                );

        return convertirRolDTO(
                rolGuardado
        );
    }

    // =====================================================
    // ACTUALIZAR ROL
    // =====================================================

    @Transactional
    public AdministracionRolDTO actualizarRol(
            Long idRol,
            RolAdministracionRequestDTO dto) {

        Long empresaId =
                obtenerEmpresaId();

        Rol rol =
                rolRepository
                        .findByIdRolAndEmpresaId(
                                idRol,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rol no encontrado"
                                )
                        );

        if (dto.getNombre() != null &&
                !dto.getNombre().isBlank()) {

            String nuevoNombre =
                    dto.getNombre()
                            .trim();

            if (!nuevoNombre.equalsIgnoreCase(
                    rol.getNombre()
            )) {

                boolean existe =
                        rolRepository
                                .existsByEmpresaIdAndNombreIgnoreCase(
                                        empresaId,
                                        nuevoNombre
                                );

                if (existe) {

                    throw new IllegalArgumentException(
                            "Ya existe un rol con ese nombre"
                    );
                }
            }

            rol.setNombre(
                    nuevoNombre
            );
        }

        if (dto.getDescripcion() != null) {

            rol.setDescripcion(
                    dto.getDescripcion()
            );
        }

        if (dto.getEstado() != null) {

            rol.setEstado(
                    dto.getEstado()
            );
        }

        if (dto.getPermisosIds() != null) {

            rol.setPermisos(
                    obtenerPermisos(
                            dto.getPermisosIds()
                    )
            );
        }

        Rol rolGuardado =
                rolRepository.save(
                        rol
                );

        return convertirRolDTO(
                rolGuardado
        );
    }

    // =====================================================
    // PERMISOS
    // =====================================================

    @Transactional(readOnly = true)
    public List<AdministracionPermisoDTO> listarPermisos() {

        return permisoRepository
                .findAllByOrderByCodigoAsc()
                .stream()
                .map(this::convertirPermisoDTO)
                .toList();
    }

    private Set<Permiso> obtenerPermisos(
            Set<Long> permisosIds) {

        Set<Permiso> permisos =
                new HashSet<>();

        if (permisosIds == null) {

            return permisos;
        }

        for (Long permisoId :
                permisosIds) {

            Permiso permiso =
                    permisoRepository
                            .findById(
                                    permisoId
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Permiso no encontrado: "
                                                    + permisoId
                                    )
                            );

            permisos.add(
                    permiso
            );
        }

        return permisos;
    }

    // =====================================================
    // CONVERTIR USUARIO
    // =====================================================

    private AdministracionUsuarioDTO convertirUsuarioDTO(
            Usuario usuario) {

        AdministracionUsuarioDTO dto =
                new AdministracionUsuarioDTO();

        dto.setIdUsuario(
                usuario.getIdUsuario()
        );

        dto.setNombre(
                usuario.getNombre()
        );

        dto.setApellido(
                usuario.getApellido()
        );

        dto.setCorreo(
                usuario.getCorreo()
        );

        dto.setEstado(
                usuario.getEstado()
        );

        Set<Long> rolesIds =
                usuario.getRoles()
                        .stream()
                        .map(
                                Rol::getIdRol
                        )
                        .collect(
                                Collectors.toSet()
                        );

        Set<String> roles =
                usuario.getRoles()
                        .stream()
                        .map(
                                Rol::getNombre
                        )
                        .collect(
                                Collectors.toSet()
                        );

        dto.setRolesIds(
                rolesIds
        );

        dto.setRoles(
                roles
        );

        return dto;
    }

    // =====================================================
    // CONVERTIR ROL
    // =====================================================

    private AdministracionRolDTO convertirRolDTO(
            Rol rol) {

        AdministracionRolDTO dto =
                new AdministracionRolDTO();

        dto.setIdRol(
                rol.getIdRol()
        );

        dto.setNombre(
                rol.getNombre()
        );

        dto.setDescripcion(
                rol.getDescripcion()
        );

        dto.setEstado(
                rol.getEstado()
        );

        Set<Long> permisosIds =
                rol.getPermisos()
                        .stream()
                        .map(
                                Permiso::getIdPermiso
                        )
                        .collect(
                                Collectors.toSet()
                        );

        Set<String> permisos =
                rol.getPermisos()
                        .stream()
                        .map(
                                Permiso::getCodigo
                        )
                        .collect(
                                Collectors.toSet()
                        );

        dto.setPermisosIds(
                permisosIds
        );

        dto.setPermisos(
                permisos
        );

        return dto;
    }

    // =====================================================
    // CONVERTIR PERMISO
    // =====================================================

    private AdministracionPermisoDTO convertirPermisoDTO(
            Permiso permiso) {

        AdministracionPermisoDTO dto =
                new AdministracionPermisoDTO();

        dto.setIdPermiso(
                permiso.getIdPermiso()
        );

        dto.setCodigo(
                permiso.getCodigo()
        );

        dto.setNombre(
                permiso.getNombre()
        );

        dto.setDescripcion(
                permiso.getDescripcion()
        );

        return dto;
    }
}
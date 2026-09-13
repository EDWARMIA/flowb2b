package com.flowb2b.administracion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.administracion.dto.ActualizarUsuarioAdministracionDTO;
import com.flowb2b.administracion.dto.AdministracionPermisoDTO;
import com.flowb2b.administracion.dto.AdministracionRolDTO;
import com.flowb2b.administracion.dto.AdministracionUsuarioDTO;
import com.flowb2b.administracion.dto.CrearUsuarioAdministracionDTO;
import com.flowb2b.administracion.dto.RolAdministracionRequestDTO;
import com.flowb2b.administracion.service.AdministracionService;

@RestController
@RequestMapping("/api/administracion")
public class AdministracionController {

    private final AdministracionService administracionService;

    public AdministracionController(
            AdministracionService administracionService) {

        this.administracionService =
                administracionService;
    }

    // =====================================================
    // USUARIOS
    // =====================================================

    @GetMapping("/usuarios")
    @PreAuthorize(
            "hasAuthority('ADMIN_USUARIO_GESTIONAR')"
    )
    public ResponseEntity<
            List<AdministracionUsuarioDTO>>
    listarUsuarios() {

        return ResponseEntity.ok(
                administracionService
                        .listarUsuarios()
        );
    }

    // =====================================================
    // CREAR USUARIO
    // =====================================================

    @PostMapping("/usuarios")
    @PreAuthorize(
            "hasAuthority('ADMIN_USUARIO_GESTIONAR')"
    )
    public ResponseEntity<
            AdministracionUsuarioDTO>
    crearUsuario(
            @RequestBody
            CrearUsuarioAdministracionDTO dto) {

        AdministracionUsuarioDTO usuario =
                administracionService
                        .crearUsuario(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    // =====================================================
    // ACTUALIZAR USUARIO
    // =====================================================

    @PutMapping("/usuarios/{idUsuario}")
    @PreAuthorize(
            "hasAuthority('ADMIN_USUARIO_GESTIONAR')"
    )
    public ResponseEntity<
            AdministracionUsuarioDTO>
    actualizarUsuario(
            @PathVariable
            Long idUsuario,

            @RequestBody
            ActualizarUsuarioAdministracionDTO dto) {

        return ResponseEntity.ok(
                administracionService
                        .actualizarUsuario(
                                idUsuario,
                                dto
                        )
        );
    }

    // =====================================================
    // ROLES
    // =====================================================

    @GetMapping("/roles")
    @PreAuthorize(
            "hasAuthority('ADMIN_ROL_GESTIONAR')"
    )
    public ResponseEntity<
            List<AdministracionRolDTO>>
    listarRoles() {

        return ResponseEntity.ok(
                administracionService
                        .listarRoles()
        );
    }

    // =====================================================
    // CREAR ROL
    // =====================================================

    @PostMapping("/roles")
    @PreAuthorize(
            "hasAuthority('ADMIN_ROL_GESTIONAR')"
    )
    public ResponseEntity<
            AdministracionRolDTO>
    crearRol(
            @RequestBody
            RolAdministracionRequestDTO dto) {

        AdministracionRolDTO rol =
                administracionService
                        .crearRol(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rol);
    }

    // =====================================================
    // ACTUALIZAR ROL
    // =====================================================

    @PutMapping("/roles/{idRol}")
    @PreAuthorize(
            "hasAuthority('ADMIN_ROL_GESTIONAR')"
    )
    public ResponseEntity<
            AdministracionRolDTO>
    actualizarRol(
            @PathVariable
            Long idRol,

            @RequestBody
            RolAdministracionRequestDTO dto) {

        return ResponseEntity.ok(
                administracionService
                        .actualizarRol(
                                idRol,
                                dto
                        )
        );
    }

    // =====================================================
    // PERMISOS
    // =====================================================

    @GetMapping("/permisos")
    @PreAuthorize(
            "hasAuthority('ADMIN_ROL_GESTIONAR')"
    )
    public ResponseEntity<
            List<AdministracionPermisoDTO>>
    listarPermisos() {

        return ResponseEntity.ok(
                administracionService
                        .listarPermisos()
        );
    }
}
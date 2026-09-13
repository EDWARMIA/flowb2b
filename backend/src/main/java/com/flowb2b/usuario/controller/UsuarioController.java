package com.flowb2b.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.usuario.dto.UsuarioRequestDTO;
import com.flowb2b.usuario.dto.UsuarioResponseDTO;
import com.flowb2b.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService =
                usuarioService;
    }

    // =========================
    // LISTAR USUARIOS
    // =========================

    @GetMapping
    @PreAuthorize(
        "hasAuthority('USUARIO_VER')"
    )
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {

        return ResponseEntity.ok(
                usuarioService.listarUsuarios()
        );
    }

    // =========================
    // BUSCAR USUARIO
    // =========================

    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('USUARIO_VER')"
    )
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }

    // =========================
    // CREAR USUARIO
    // =========================

    @PostMapping
    @PreAuthorize(
        "hasAuthority('USUARIO_CREAR')"
    )
    public ResponseEntity<UsuarioResponseDTO> crear(
            @Valid
            @RequestBody UsuarioRequestDTO dto) {

        UsuarioResponseDTO usuario =
                usuarioService.crearUsuario(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }
}
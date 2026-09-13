package com.flowb2b.auditoria.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.auditoria.dto.AuditoriaResponseDTO;
import com.flowb2b.auditoria.service.AuditoriaService;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(
            AuditoriaService auditoriaService) {

        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('EMPRESA_VER') "
            + "or hasAuthority('USUARIO_VER')")
    public ResponseEntity<List<AuditoriaResponseDTO>>
            listar() {

        return ResponseEntity.ok(
                auditoriaService.listar());
    }

    @GetMapping("/entidad/{entidad}/{entidadId}")
    @PreAuthorize(
            "hasAuthority('EMPRESA_VER') "
            + "or hasAuthority('USUARIO_VER')")
    public ResponseEntity<List<AuditoriaResponseDTO>>
            listarPorEntidad(
                    @PathVariable String entidad,
                    @PathVariable Long entidadId) {

        return ResponseEntity.ok(
                auditoriaService
                        .listarPorEntidad(
                                entidad,
                                entidadId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize(
            "hasAuthority('EMPRESA_VER') "
            + "or hasAuthority('USUARIO_VER')")
    public ResponseEntity<List<AuditoriaResponseDTO>>
            listarPorUsuario(
                    @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                auditoriaService
                        .listarPorUsuario(usuarioId));
    }

    @GetMapping("/accion/{accion}")
    @PreAuthorize(
            "hasAuthority('EMPRESA_VER') "
            + "or hasAuthority('USUARIO_VER')")
    public ResponseEntity<List<AuditoriaResponseDTO>>
            listarPorAccion(
                    @PathVariable String accion) {

        return ResponseEntity.ok(
                auditoriaService
                        .listarPorAccion(accion));
    }
}
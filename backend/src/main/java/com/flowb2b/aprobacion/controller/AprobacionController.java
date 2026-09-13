package com.flowb2b.aprobacion.controller;

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

import com.flowb2b.aprobacion.dto.AprobacionResponseDTO;
import com.flowb2b.aprobacion.dto.ResponderAprobacionRequestDTO;
import com.flowb2b.aprobacion.dto.SolicitudAprobacionRequestDTO;
import com.flowb2b.aprobacion.service.AprobacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aprobaciones")
public class AprobacionController {

    private final AprobacionService aprobacionService;

    public AprobacionController(AprobacionService aprobacionService) {
        this.aprobacionService = aprobacionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COTIZACION_APROBAR')")
    public ResponseEntity<List<AprobacionResponseDTO>> listar() {

        return ResponseEntity.ok(
                aprobacionService.listar()
        );
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAuthority('COTIZACION_APROBAR')")
    public ResponseEntity<List<AprobacionResponseDTO>> listarPendientes() {

        return ResponseEntity.ok(
                aprobacionService.listarPendientes()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COTIZACION_APROBAR')")
    public ResponseEntity<AprobacionResponseDTO> obtenerPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                aprobacionService.obtenerPorId(id)
        );
    }

    @PostMapping("/solicitar")
    @PreAuthorize("hasAuthority('COTIZACION_EDITAR')")
    public ResponseEntity<AprobacionResponseDTO> solicitar(
            @Valid @RequestBody SolicitudAprobacionRequestDTO dto) {

        AprobacionResponseDTO respuesta =
                aprobacionService.solicitar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAuthority('COTIZACION_APROBAR')")
    public ResponseEntity<AprobacionResponseDTO> aprobar(
            @PathVariable Long id,
            @Valid @RequestBody ResponderAprobacionRequestDTO dto) {

        return ResponseEntity.ok(
                aprobacionService.aprobar(id, dto)
        );
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAuthority('COTIZACION_APROBAR')")
    public ResponseEntity<AprobacionResponseDTO> rechazar(
            @PathVariable Long id,
            @Valid @RequestBody ResponderAprobacionRequestDTO dto) {

        return ResponseEntity.ok(
                aprobacionService.rechazar(id, dto)
        );
    }
}
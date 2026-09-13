package com.flowb2b.solicitud.controller;

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

import com.flowb2b.solicitud.dto.SolicitudRequestDTO;
import com.flowb2b.solicitud.dto.SolicitudResponseDTO;
import com.flowb2b.solicitud.service.SolicitudService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(
            SolicitudService solicitudService) {

        this.solicitudService = solicitudService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SOLICITUD_VER')")
    public ResponseEntity<List<SolicitudResponseDTO>>
            listar() {

        return ResponseEntity.ok(
                solicitudService.listar()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SOLICITUD_VER')")
    public ResponseEntity<SolicitudResponseDTO>
            obtenerPorId(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                solicitudService.obtenerPorId(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SOLICITUD_CREAR')")
    public ResponseEntity<SolicitudResponseDTO>
            crear(
                    @Valid
                    @RequestBody
                    SolicitudRequestDTO dto) {

        SolicitudResponseDTO respuesta =
                solicitudService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }
}
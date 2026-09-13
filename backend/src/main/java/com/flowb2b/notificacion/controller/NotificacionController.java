package com.flowb2b.notificacion.controller;

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

import com.flowb2b.notificacion.dto.NotificacionRequestDTO;
import com.flowb2b.notificacion.dto.NotificacionResponseDTO;
import com.flowb2b.notificacion.service.NotificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(
            NotificacionService notificacionService) {

        this.notificacionService = notificacionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICACION_VER')")
    public ResponseEntity<List<NotificacionResponseDTO>>
            listarMisNotificaciones() {

        List<NotificacionResponseDTO> notificaciones =
                notificacionService.listarMisNotificaciones();

        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/no-leidas")
    @PreAuthorize("hasAuthority('NOTIFICACION_VER')")
    public ResponseEntity<List<NotificacionResponseDTO>>
            listarMisNoLeidas() {

        List<NotificacionResponseDTO> notificaciones =
                notificacionService.listarMisNoLeidas();

        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/{idNotificacion}")
    @PreAuthorize("hasAuthority('NOTIFICACION_VER')")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(
            @PathVariable Long idNotificacion) {

        NotificacionResponseDTO notificacion =
                notificacionService.obtenerPorId(idNotificacion);

        return ResponseEntity.ok(notificacion);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TAREA_GESTIONAR')")
    public ResponseEntity<NotificacionResponseDTO> crear(
            @Valid @RequestBody NotificacionRequestDTO dto) {

        NotificacionResponseDTO notificacion =
                notificacionService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificacion);
    }

    @PutMapping("/{idNotificacion}/leer")
    @PreAuthorize("hasAuthority('NOTIFICACION_VER')")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(
            @PathVariable Long idNotificacion) {

        NotificacionResponseDTO notificacion =
                notificacionService.marcarComoLeida(
                        idNotificacion);

        return ResponseEntity.ok(notificacion);
    }
}
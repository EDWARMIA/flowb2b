package com.flowb2b.historial.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.historial.dto.HistorialEstadoResponseDTO;
import com.flowb2b.historial.service.HistorialEstadoService;

@RestController
@RequestMapping("/api/historial")
public class HistorialEstadoController {

    private final HistorialEstadoService historialEstadoService;

    public HistorialEstadoController(
            HistorialEstadoService historialEstadoService) {

        this.historialEstadoService = historialEstadoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TAREA_VER') or hasAuthority('COTIZACION_VER') or hasAuthority('PEDIDO_VER')")
    public ResponseEntity<List<HistorialEstadoResponseDTO>> listar() {

        List<HistorialEstadoResponseDTO> historiales =
                historialEstadoService.listar();

        return ResponseEntity.ok(historiales);
    }

    @GetMapping("/{tipoEntidad}/{entidadId}")
    @PreAuthorize("hasAuthority('TAREA_VER') or hasAuthority('COTIZACION_VER') or hasAuthority('PEDIDO_VER')")
    public ResponseEntity<List<HistorialEstadoResponseDTO>> listarPorEntidad(
            @PathVariable String tipoEntidad,
            @PathVariable Long entidadId) {

        List<HistorialEstadoResponseDTO> historiales =
                historialEstadoService.listarPorEntidad(
                        tipoEntidad,
                        entidadId);

        return ResponseEntity.ok(historiales);
    }
}
package com.flowb2b.tarea.controller;

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

import com.flowb2b.tarea.dto.TareaEstadoRequestDTO;
import com.flowb2b.tarea.dto.TareaRequestDTO;
import com.flowb2b.tarea.dto.TareaResponseDTO;
import com.flowb2b.tarea.service.TareaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TAREA_VER')")
    public ResponseEntity<List<TareaResponseDTO>> listar() {

        List<TareaResponseDTO> tareas =
                tareaService.listar();

        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/{idTarea}")
    @PreAuthorize("hasAuthority('TAREA_VER')")
    public ResponseEntity<TareaResponseDTO> obtenerPorId(
            @PathVariable Long idTarea) {

        TareaResponseDTO tarea =
                tareaService.obtenerPorId(idTarea);

        return ResponseEntity.ok(tarea);
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAuthority('TAREA_VER')")
    public ResponseEntity<List<TareaResponseDTO>> listarPorPedido(
            @PathVariable Long pedidoId) {

        List<TareaResponseDTO> tareas =
                tareaService.listarPorPedido(pedidoId);

        return ResponseEntity.ok(tareas);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('TAREA_GESTIONAR')")
    public ResponseEntity<TareaResponseDTO> crear(
            @Valid @RequestBody TareaRequestDTO dto) {

        TareaResponseDTO tarea =
                tareaService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarea);
    }

    @PutMapping("/{idTarea}/estado")
    @PreAuthorize("hasAuthority('TAREA_GESTIONAR')")
    public ResponseEntity<TareaResponseDTO> cambiarEstado(
            @PathVariable Long idTarea,
            @Valid @RequestBody TareaEstadoRequestDTO dto) {

        TareaResponseDTO tarea =
                tareaService.cambiarEstado(idTarea, dto);

        return ResponseEntity.ok(tarea);
    }
}
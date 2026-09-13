package com.flowb2b.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.inventario.dto.InventarioRequestDTO;
import com.flowb2b.inventario.dto.InventarioResponseDTO;
import com.flowb2b.inventario.service.InventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(
            InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public ResponseEntity<List<InventarioResponseDTO>> listar() {

        return ResponseEntity.ok(
                inventarioService.listarInventario()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public ResponseEntity<InventarioResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                inventarioService.buscarPorId(id)
        );
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasAuthority('INVENTARIO_VER')")
    public ResponseEntity<InventarioResponseDTO> buscarPorProducto(
            @PathVariable Long idProducto) {

        return ResponseEntity.ok(
                inventarioService.buscarPorProducto(
                        idProducto
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTARIO_EDITAR')")
    public ResponseEntity<InventarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequestDTO dto) {

        return ResponseEntity.ok(
                inventarioService.actualizarInventario(
                        id,
                        dto
                )
        );
    }
}
package com.flowb2b.producto.controller;

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

import com.flowb2b.producto.dto.CategoriaProductoRequestDTO;
import com.flowb2b.producto.dto.CategoriaProductoResponseDTO;
import com.flowb2b.producto.service.CategoriaProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias-producto")
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaService;

    public CategoriaProductoController(
            CategoriaProductoService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTO_VER')")
    public ResponseEntity<List<CategoriaProductoResponseDTO>> listar() {
        return ResponseEntity.ok(
                categoriaService.listarCategorias()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_VER')")
    public ResponseEntity<CategoriaProductoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                categoriaService.buscarPorId(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTO_CREAR')")
    public ResponseEntity<CategoriaProductoResponseDTO> crear(
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    categoriaService.crearCategoria(dto)
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_EDITAR')")
    public ResponseEntity<CategoriaProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        return ResponseEntity.ok(
                categoriaService.actualizarCategoria(
                        id,
                        dto
                )
        );
    }
}
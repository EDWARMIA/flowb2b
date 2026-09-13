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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.producto.dto.ProductoRequestDTO;
import com.flowb2b.producto.dto.ProductoResponseDTO;
import com.flowb2b.producto.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTO_VER')")
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @RequestParam(required = false) Long categoriaId) {

        if (categoriaId != null) {
            return ResponseEntity.ok(
                    productoService.listarPorCategoria(
                            categoriaId
                    )
            );
        }

        return ResponseEntity.ok(
                productoService.listarProductos()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_VER')")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productoService.buscarPorId(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCTO_CREAR')")
    public ResponseEntity<ProductoResponseDTO> crear(
            @Valid @RequestBody ProductoRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    productoService.crearProducto(dto)
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTO_EDITAR')")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {

        return ResponseEntity.ok(
                productoService.actualizarProducto(
                        id,
                        dto
                )
        );
    }
}
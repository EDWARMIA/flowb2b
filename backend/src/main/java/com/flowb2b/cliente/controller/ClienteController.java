package com.flowb2b.cliente.controller;

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

import com.flowb2b.cliente.dto.ClienteRequestDTO;
import com.flowb2b.cliente.dto.ClienteResponseDTO;
import com.flowb2b.cliente.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(
            ClienteService clienteService) {

        this.clienteService = clienteService;
    }

    // =========================
    // LISTAR CLIENTES
    // =========================

    @GetMapping
    @PreAuthorize(
        "hasAuthority('CLIENTE_VER')"
    )
    public ResponseEntity<List<ClienteResponseDTO>> listar() {

        return ResponseEntity.ok(
                clienteService.listarClientes()
        );
    }

    // =========================
    // BUSCAR CLIENTE POR ID
    // =========================

    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('CLIENTE_VER')"
    )
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                clienteService.buscarPorId(id)
        );
    }

    // =========================
    // CREAR CLIENTE
    // =========================

    @PostMapping
    @PreAuthorize(
        "hasAuthority('CLIENTE_CREAR')"
    )
    public ResponseEntity<ClienteResponseDTO> crear(
            @Valid
            @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO cliente =
                clienteService.crearCliente(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cliente);
    }

    // =========================
    // ACTUALIZAR CLIENTE
    // =========================

    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('CLIENTE_EDITAR')"
    )
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody ClienteRequestDTO dto) {

        return ResponseEntity.ok(
                clienteService.actualizarCliente(
                        id,
                        dto
                )
        );
    }
}
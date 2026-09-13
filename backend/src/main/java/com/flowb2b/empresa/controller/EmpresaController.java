package com.flowb2b.empresa.controller;

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

import com.flowb2b.empresa.dto.EmpresaRequestDTO;
import com.flowb2b.empresa.dto.EmpresaResponseDTO;
import com.flowb2b.empresa.service.EmpresaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(
            EmpresaService empresaService) {

        this.empresaService =
                empresaService;
    }

    // =========================
    // LISTAR EMPRESAS
    // =========================

    @GetMapping
    @PreAuthorize(
        "hasAuthority('EMPRESA_VER')"
    )
    public ResponseEntity<List<EmpresaResponseDTO>> listar() {

        return ResponseEntity.ok(
                empresaService.listarEmpresas()
        );
    }

    // =========================
    // BUSCAR EMPRESA
    // =========================

    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('EMPRESA_VER')"
    )
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                empresaService.buscarPorId(id)
        );
    }

    // =========================
    // CREAR EMPRESA
    // =========================

    @PostMapping
    @PreAuthorize(
        "hasAuthority('EMPRESA_EDITAR')"
    )
    public ResponseEntity<EmpresaResponseDTO> crear(
            @Valid
            @RequestBody EmpresaRequestDTO dto) {

        EmpresaResponseDTO empresa =
                empresaService.crearEmpresa(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empresa);
    }

    // =========================
    // ACTUALIZAR EMPRESA
    // =========================

    @PutMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('EMPRESA_EDITAR')"
    )
    public ResponseEntity<EmpresaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid
            @RequestBody EmpresaRequestDTO dto) {

        return ResponseEntity.ok(
                empresaService.actualizarEmpresa(
                    id,
                    dto
                )
        );
    }
}
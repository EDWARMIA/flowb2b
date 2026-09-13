package com.flowb2b.cotizacion.controller;

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

import com.flowb2b.cotizacion.dto.CotizacionRequestDTO;
import com.flowb2b.cotizacion.dto.CotizacionResponseDTO;
import com.flowb2b.cotizacion.service.CotizacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(
            CotizacionService cotizacionService) {

        this.cotizacionService =
                cotizacionService;
    }

    @GetMapping
    @PreAuthorize(
        "hasAuthority('COTIZACION_VER')"
    )
    public ResponseEntity<List<CotizacionResponseDTO>>
            listar() {

        return ResponseEntity.ok(
                cotizacionService.listar()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
        "hasAuthority('COTIZACION_VER')"
    )
    public ResponseEntity<CotizacionResponseDTO>
            obtenerPorId(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                cotizacionService.obtenerPorId(id)
        );
    }

    @PostMapping
    @PreAuthorize(
        "hasAuthority('COTIZACION_CREAR')"
    )
    public ResponseEntity<CotizacionResponseDTO>
            crear(
                    @Valid
                    @RequestBody
                    CotizacionRequestDTO dto) {

        CotizacionResponseDTO respuesta =
                cotizacionService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    @PutMapping("/{id}/aceptar")
    @PreAuthorize(
        "hasAuthority('COTIZACION_EDITAR')"
    )
    public ResponseEntity<CotizacionResponseDTO>
            aceptar(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                cotizacionService.aceptar(id)
        );
    }
}
package com.flowb2b.pedido.controller;

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

import com.flowb2b.pedido.dto.PedidoEstadoRequestDTO;
import com.flowb2b.pedido.dto.PedidoRequestDTO;
import com.flowb2b.pedido.dto.PedidoResponseDTO;
import com.flowb2b.pedido.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(
            PedidoService pedidoService) {

        this.pedidoService =
                pedidoService;
    }

    @GetMapping
    @PreAuthorize(
        "hasAuthority('PEDIDO_VER')"
    )
    public ResponseEntity<List<PedidoResponseDTO>>
            listar() {

        List<PedidoResponseDTO> pedidos =
                pedidoService.listar();

        return ResponseEntity.ok(
                pedidos
        );
    }

    @GetMapping("/{idPedido}")
    @PreAuthorize(
        "hasAuthority('PEDIDO_VER')"
    )
    public ResponseEntity<PedidoResponseDTO>
            obtenerPorId(
                    @PathVariable
                    Long idPedido) {

        PedidoResponseDTO pedido =
                pedidoService.obtenerPorId(
                        idPedido
                );

        return ResponseEntity.ok(
                pedido
        );
    }

    @PostMapping
    @PreAuthorize(
        "hasAuthority('PEDIDO_GESTIONAR')"
    )
    public ResponseEntity<PedidoResponseDTO>
            crear(
                    @Valid
                    @RequestBody
                    PedidoRequestDTO dto) {

        PedidoResponseDTO pedido =
                pedidoService.crear(
                        dto
                );

        return ResponseEntity
                .status(
                    HttpStatus.CREATED
                )
                .body(
                    pedido
                );
    }

    @PutMapping(
        "/{idPedido}/reintentar-reserva"
    )
    @PreAuthorize(
        "hasAuthority('PEDIDO_GESTIONAR')"
    )
    public ResponseEntity<PedidoResponseDTO>
            reintentarReserva(
                    @PathVariable
                    Long idPedido) {

        PedidoResponseDTO pedido =
                pedidoService
                    .reintentarReserva(
                        idPedido
                    );

        return ResponseEntity.ok(
                pedido
        );
    }

    @PutMapping(
        "/{idPedido}/estado"
    )
    @PreAuthorize(
        "hasAuthority('PEDIDO_GESTIONAR')"
    )
    public ResponseEntity<PedidoResponseDTO>
            cambiarEstado(
                    @PathVariable
                    Long idPedido,

                    @Valid
                    @RequestBody
                    PedidoEstadoRequestDTO dto) {

        PedidoResponseDTO pedido =
                pedidoService
                    .cambiarEstado(
                        idPedido,
                        dto
                    );

        return ResponseEntity.ok(
                pedido
        );
    }
}
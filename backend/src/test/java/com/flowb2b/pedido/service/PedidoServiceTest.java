package com.flowb2b.pedido.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.cotizacion.repository.DetalleCotizacionRepository;
import com.flowb2b.historial.service.HistorialEstadoService;
import com.flowb2b.inventario.repository.InventarioRepository;
import com.flowb2b.pedido.dto.PedidoEstadoRequestDTO;
import com.flowb2b.pedido.entity.DetallePedido;
import com.flowb2b.pedido.entity.Pedido;
import com.flowb2b.pedido.repository.DetallePedidoRepository;
import com.flowb2b.pedido.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    private static final Long EMPRESA_ID = 1L;
    private static final Long PEDIDO_ID = 10L;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private HistorialEstadoService historialEstadoService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void configurarAutenticacion() {

        UsuarioAutenticado usuario =
                mock(UsuarioAutenticado.class);

        when(usuario.getEmpresaId())
                .thenReturn(EMPRESA_ID);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void limpiarAutenticacion() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void cambiarEstado_deberiaCambiarDeCreadoAEnPreparacion() {

        Pedido pedido = crearPedido(
                "CREADO"
        );

        PedidoEstadoRequestDTO request =
                crearRequestEstado(
                        "EN_PREPARACION",
                        "Pedido enviado a preparaciÃ³n"
                );

        when(
                pedidoRepository
                        .findByIdPedidoAndEmpresaId(
                                PEDIDO_ID,
                                EMPRESA_ID
                        )
        ).thenReturn(
                Optional.of(pedido)
        );

        when(
                pedidoRepository.save(
                        any(Pedido.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                detallePedidoRepository
                        .findByPedidoId(PEDIDO_ID)
        ).thenReturn(
                List.of()
        );

        var response =
                pedidoService.cambiarEstado(
                        PEDIDO_ID,
                        request
                );

        assertEquals(
                "EN_PREPARACION",
                response.getEstado()
        );

        assertEquals(
                PEDIDO_ID,
                response.getIdPedido()
        );

        verify(pedidoRepository)
                .save(
                        any(Pedido.class)
                );

        verify(historialEstadoService)
                .registrar(
                        "PEDIDO",
                        PEDIDO_ID,
                        "CREADO",
                        "EN_PREPARACION",
                        "Pedido enviado a preparaciÃ³n"
                );
    }

    @Test
    void cambiarEstado_deberiaRechazarTransicionDeCreadoAEntregado() {

        Pedido pedido = crearPedido(
                "CREADO"
        );

        PedidoEstadoRequestDTO request =
                crearRequestEstado(
                        "ENTREGADO",
                        "Intento de transiciÃ³n invÃ¡lida"
                );

        when(
                pedidoRepository
                        .findByIdPedidoAndEmpresaId(
                                PEDIDO_ID,
                                EMPRESA_ID
                        )
        ).thenReturn(
                Optional.of(pedido)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pedidoService.cambiarEstado(
                                        PEDIDO_ID,
                                        request
                                )
                );

        assertEquals(
                "No se permite cambiar el pedido de CREADO a ENTREGADO",
                exception.getMessage()
        );
    }

    @Test
    void cambiarEstado_deberiaRechazarDespachoCuandoExisteCantidadPendiente() {

        Pedido pedido = crearPedido(
                "EN_PREPARACION"
        );

        PedidoEstadoRequestDTO request =
                crearRequestEstado(
                        "DESPACHADO",
                        "Intento de despacho"
                );

        DetallePedido detalle =
                new DetallePedido();

        detalle.setIdDetalle(100L);
        detalle.setPedidoId(PEDIDO_ID);
        detalle.setProductoId(3L);
        detalle.setDescripcion(
                "Producto de prueba"
        );

        detalle.setCantidad(
                new BigDecimal("10.00")
        );

        detalle.setPrecioUnitario(
                new BigDecimal("100.00")
        );

        detalle.setCantidadReservada(
                new BigDecimal("8.00")
        );

        detalle.setCantidadPendiente(
                new BigDecimal("2.00")
        );

        detalle.setSubtotal(
                new BigDecimal("1000.00")
        );

        when(
                pedidoRepository
                        .findByIdPedidoAndEmpresaId(
                                PEDIDO_ID,
                                EMPRESA_ID
                        )
        ).thenReturn(
                Optional.of(pedido)
        );

        when(
                detallePedidoRepository
                        .findByPedidoId(PEDIDO_ID)
        ).thenReturn(
                new ArrayList<>(List.of(detalle))
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                pedidoService.cambiarEstado(
                                        PEDIDO_ID,
                                        request
                                )
                );

        assertEquals(
                "No se puede despachar el pedido. "
                        + "El producto 3 tiene 2.00 unidades pendientes",
                exception.getMessage()
        );
    }

    private Pedido crearPedido(
            String estado) {

        Pedido pedido =
                new Pedido();

        pedido.setIdPedido(
                PEDIDO_ID
        );

        pedido.setEmpresaId(
                EMPRESA_ID
        );

        pedido.setCotizacionId(
                20L
        );

        pedido.setClienteId(
                2L
        );

        pedido.setCodigo(
                "PED-TEST-001"
        );

        pedido.setEstado(
                estado
        );

        pedido.setSubtotal(
                new BigDecimal("1000.00")
        );

        pedido.setDescuento(
                new BigDecimal("50.00")
        );

        pedido.setImpuesto(
                BigDecimal.ZERO
        );

        pedido.setTotal(
                new BigDecimal("950.00")
        );

        pedido.setObservaciones(
                "Pedido utilizado para pruebas"
        );

        return pedido;
    }

    private PedidoEstadoRequestDTO crearRequestEstado(
            String estado,
            String comentario) {

        PedidoEstadoRequestDTO request =
                new PedidoEstadoRequestDTO();

        request.setEstado(
                estado
        );

        request.setComentario(
                comentario
        );

        return request;
    }
}

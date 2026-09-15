package com.flowb2b.inventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
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
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.inventario.dto.InventarioRequestDTO;
import com.flowb2b.inventario.dto.InventarioResponseDTO;
import com.flowb2b.inventario.entity.Inventario;
import com.flowb2b.inventario.repository.InventarioRepository;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private InventarioService inventarioService;

    private static final Long EMPRESA_ID = 1L;
    private static final Long USUARIO_ID = 4L;
    private static final Long INVENTARIO_ID = 1L;
    private static final Long PRODUCTO_ID = 3L;

    @BeforeEach
    void configurarUsuarioAutenticado() {

        UsuarioAutenticado usuario = new UsuarioAutenticado(
                USUARIO_ID,
                EMPRESA_ID,
                "usuario@test.com");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        Collections.emptyList());

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void limpiarContextoSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void actualizarInventario_deberiaActualizarCorrectamenteCuandoDatosSonValidos() {

        Inventario inventario = crearInventario();
        Producto producto = crearProductoActivo();

        InventarioRequestDTO request = crearRequest(
                new BigDecimal("100.00"),
                new BigDecimal("15.00"),
                new BigDecimal("20.00"));

        when(inventarioRepository.findByIdInventarioAndEmpresaId(
                INVENTARIO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(inventario));

        when(productoRepository.findByIdProductoAndEmpresaId(
                PRODUCTO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(producto));

        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventarioResponseDTO response =
                inventarioService.actualizarInventario(
                        INVENTARIO_ID,
                        request);

        assertEquals(
                new BigDecimal("100.00"),
                response.getStockActual());

        assertEquals(
                new BigDecimal("15.00"),
                response.getStockReservado());

        assertEquals(
                new BigDecimal("85.00"),
                response.getStockDisponible());

        assertEquals(
                new BigDecimal("20.00"),
                response.getStockMinimo());

        verify(inventarioRepository)
                .save(any(Inventario.class));

        verify(auditoriaService)
                .registrar(
                        "ACTUALIZAR",
                        "INVENTARIO",
                        INVENTARIO_ID,
                        "{\"productoId\":3,\"stockActual\":50.00,\"stockReservado\":5.00,\"stockDisponible\":45.00,\"stockMinimo\":10.00}",
                        "{\"productoId\":3,\"stockActual\":100.00,\"stockReservado\":15.00,\"stockDisponible\":85.00,\"stockMinimo\":20.00}",
                        null);
    }

    @Test
    void actualizarInventario_deberiaLanzarErrorCuandoStockReservadoSuperaStockActual() {

        Inventario inventario = crearInventario();
        Producto producto = crearProductoActivo();

        InventarioRequestDTO request = crearRequest(
                new BigDecimal("100.00"),
                new BigDecimal("120.00"),
                new BigDecimal("20.00"));

        when(inventarioRepository.findByIdInventarioAndEmpresaId(
                INVENTARIO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(inventario));

        when(productoRepository.findByIdProductoAndEmpresaId(
                PRODUCTO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(producto));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> inventarioService.actualizarInventario(
                                INVENTARIO_ID,
                                request));

        assertEquals(
                "El stock reservado no puede ser mayor que el stock actual",
                exception.getMessage());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    @Test
    void actualizarInventario_deberiaLanzarErrorCuandoStockActualEsNegativo() {

        Inventario inventario = crearInventario();
        Producto producto = crearProductoActivo();

        InventarioRequestDTO request = crearRequest(
                new BigDecimal("-1.00"),
                BigDecimal.ZERO,
                new BigDecimal("10.00"));

        when(inventarioRepository.findByIdInventarioAndEmpresaId(
                INVENTARIO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(inventario));

        when(productoRepository.findByIdProductoAndEmpresaId(
                PRODUCTO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(producto));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> inventarioService.actualizarInventario(
                                INVENTARIO_ID,
                                request));

        assertEquals(
                "El stock actual no puede ser negativo",
                exception.getMessage());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    @Test
    void actualizarInventario_deberiaLanzarErrorCuandoProductoEstaInactivo() {

        Inventario inventario = crearInventario();

        Producto producto = crearProductoActivo();
        producto.setEstado(false);

        InventarioRequestDTO request = crearRequest(
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"));

        when(inventarioRepository.findByIdInventarioAndEmpresaId(
                INVENTARIO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(inventario));

        when(productoRepository.findByIdProductoAndEmpresaId(
                PRODUCTO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.of(producto));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> inventarioService.actualizarInventario(
                                INVENTARIO_ID,
                                request));

        assertEquals(
                "El producto se encuentra inactivo",
                exception.getMessage());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    @Test
    void actualizarInventario_deberiaLanzarErrorCuandoInventarioNoPerteneceAEmpresa() {

        InventarioRequestDTO request = crearRequest(
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"));

        when(inventarioRepository.findByIdInventarioAndEmpresaId(
                INVENTARIO_ID,
                EMPRESA_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> inventarioService.actualizarInventario(
                                INVENTARIO_ID,
                                request));

        assertEquals(
                "Inventario no encontrado",
                exception.getMessage());

        verify(productoRepository, never())
                .findByIdProductoAndEmpresaId(
                        any(),
                        any());

        verify(inventarioRepository, never())
                .save(any(Inventario.class));
    }

    private Inventario crearInventario() {

        Inventario inventario = new Inventario();

        inventario.setIdInventario(INVENTARIO_ID);
        inventario.setEmpresaId(EMPRESA_ID);
        inventario.setProductoId(PRODUCTO_ID);
        inventario.setStockActual(new BigDecimal("50.00"));
        inventario.setStockReservado(new BigDecimal("5.00"));
        inventario.setStockMinimo(new BigDecimal("10.00"));

        return inventario;
    }

    private Producto crearProductoActivo() {

        Producto producto = new Producto();

        producto.setIdProducto(PRODUCTO_ID);
        producto.setEmpresaId(EMPRESA_ID);
        producto.setCategoriaId(1L);
        producto.setCodigo("PROD-003");
        producto.setNombre("Producto de prueba");
        producto.setDescripcion("Producto utilizado en pruebas");
        producto.setUnidadMedida("UNIDAD");
        producto.setPrecioBase(new BigDecimal("100.00"));
        producto.setEstado(true);

        return producto;
    }

    private InventarioRequestDTO crearRequest(
            BigDecimal stockActual,
            BigDecimal stockReservado,
            BigDecimal stockMinimo) {

        InventarioRequestDTO request =
                new InventarioRequestDTO();

        request.setProductoId(PRODUCTO_ID);
        request.setStockActual(stockActual);
        request.setStockReservado(stockReservado);
        request.setStockMinimo(stockMinimo);

        return request;
    }
}
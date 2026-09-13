package com.flowb2b.inventario.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.inventario.dto.InventarioRequestDTO;
import com.flowb2b.inventario.dto.InventarioResponseDTO;
import com.flowb2b.inventario.entity.Inventario;
import com.flowb2b.inventario.repository.InventarioRepository;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.ProductoRepository;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final AuditoriaService auditoriaService;

    public InventarioService(
            InventarioRepository inventarioRepository,
            ProductoRepository productoRepository,
            AuditoriaService auditoriaService) {

        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarInventario() {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        return inventarioRepository
                .findByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO buscarPorId(
            Long idInventario) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        Inventario inventario =
                inventarioRepository
                        .findByIdInventarioAndEmpresaId(
                                idInventario,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventario no encontrado"));

        return convertirAResponseDTO(
                inventario);
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO buscarPorProducto(
            Long idProducto) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        validarProductoPerteneceAEmpresa(
                idProducto,
                empresaId);

        Inventario inventario =
                inventarioRepository
                        .findByProductoIdAndEmpresaId(
                                idProducto,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventario no encontrado"));

        return convertirAResponseDTO(
                inventario);
    }

    @Transactional
    public InventarioResponseDTO actualizarInventario(
            Long idInventario,
            InventarioRequestDTO dto) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        Inventario inventario =
                inventarioRepository
                        .findByIdInventarioAndEmpresaId(
                                idInventario,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventario no encontrado"));

        validarProductoPerteneceAEmpresa(
                dto.getProductoId(),
                empresaId);

        if (!inventario.getProductoId()
                .equals(dto.getProductoId())) {

            throw new IllegalArgumentException(
                    "No se puede cambiar el producto asociado al inventario");
        }

        validarStocks(dto);

        // ================================================
        // CAPTURAR DATOS ANTERIORES
        // ================================================

        String datosAnteriores =
                construirJsonInventario(
                        inventario);

        // ================================================
        // ACTUALIZAR INVENTARIO
        // ================================================

        inventario.setStockActual(
                dto.getStockActual());

        inventario.setStockReservado(
                dto.getStockReservado());

        inventario.setStockMinimo(
                dto.getStockMinimo());

        Inventario actualizado =
                inventarioRepository.save(
                        inventario);

        // ================================================
        // AUDITORÍA
        // ================================================

        String datosNuevos =
                construirJsonInventario(
                        actualizado);

        auditoriaService.registrar(
                "ACTUALIZAR",
                "INVENTARIO",
                actualizado.getIdInventario(),
                datosAnteriores,
                datosNuevos,
                null);

        return convertirAResponseDTO(
                actualizado);
    }

    // =====================================================
    // VALIDAR STOCKS
    // =====================================================

    private void validarStocks(
            InventarioRequestDTO dto) {

        if (dto.getStockActual() == null) {

            throw new IllegalArgumentException(
                    "El stock actual es obligatorio");
        }

        if (dto.getStockReservado() == null) {

            throw new IllegalArgumentException(
                    "El stock reservado es obligatorio");
        }

        if (dto.getStockMinimo() == null) {

            throw new IllegalArgumentException(
                    "El stock mínimo es obligatorio");
        }

        if (dto.getStockActual()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El stock actual no puede ser negativo");
        }

        if (dto.getStockReservado()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El stock reservado no puede ser negativo");
        }

        if (dto.getStockMinimo()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo");
        }

        if (dto.getStockReservado()
                .compareTo(dto.getStockActual()) > 0) {

            throw new IllegalArgumentException(
                    "El stock reservado no puede ser mayor que el stock actual");
        }
    }

    // =====================================================
    // VALIDAR PRODUCTO
    // =====================================================

    private void validarProductoPerteneceAEmpresa(
            Long idProducto,
            Long empresaId) {

        Producto producto =
                productoRepository
                        .findByIdProductoAndEmpresaId(
                                idProducto,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Producto no encontrado"));

        if (!Boolean.TRUE.equals(
                producto.getEstado())) {

            throw new IllegalArgumentException(
                    "El producto se encuentra inactivo");
        }
    }

    // =====================================================
    // JSON PARA AUDITORÍA
    // =====================================================

    private String construirJsonInventario(
            Inventario inventario) {

        BigDecimal stockActual =
                valorSeguro(
                        inventario.getStockActual());

        BigDecimal stockReservado =
                valorSeguro(
                        inventario.getStockReservado());

        BigDecimal stockMinimo =
                valorSeguro(
                        inventario.getStockMinimo());

        BigDecimal stockDisponible =
                stockActual.subtract(
                        stockReservado);

        return "{"
                + "\"productoId\":"
                + inventario.getProductoId()
                + ","
                + "\"stockActual\":"
                + stockActual
                + ","
                + "\"stockReservado\":"
                + stockReservado
                + ","
                + "\"stockDisponible\":"
                + stockDisponible
                + ","
                + "\"stockMinimo\":"
                + stockMinimo
                + "}";
    }

    // =====================================================
    // VALOR SEGURO BIGDECIMAL
    // =====================================================

    private BigDecimal valorSeguro(
            BigDecimal valor) {

        if (valor == null) {
            return BigDecimal.ZERO;
        }

        return valor;
    }

    // =====================================================
    // EMPRESA DEL USUARIO AUTENTICADO
    // =====================================================

    private Long obtenerEmpresaIdAutenticada() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado");
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "No se pudo obtener la información del usuario autenticado");
        }

        UsuarioAutenticado usuario =
                (UsuarioAutenticado) principal;

        return usuario.getEmpresaId();
    }

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================

    private InventarioResponseDTO convertirAResponseDTO(
            Inventario inventario) {

        InventarioResponseDTO dto =
                new InventarioResponseDTO();

        dto.setIdInventario(
                inventario.getIdInventario());

        dto.setEmpresaId(
                inventario.getEmpresaId());

        dto.setProductoId(
                inventario.getProductoId());

        dto.setStockActual(
                inventario.getStockActual());

        dto.setStockReservado(
                inventario.getStockReservado());

        dto.setStockMinimo(
                inventario.getStockMinimo());

        BigDecimal stockActual =
                valorSeguro(
                        inventario.getStockActual());

        BigDecimal stockReservado =
                valorSeguro(
                        inventario.getStockReservado());

        BigDecimal disponible =
                stockActual.subtract(
                        stockReservado);

        dto.setStockDisponible(
                disponible);

        dto.setFechaActualizacion(
                inventario.getFechaActualizacion());

        return dto;
    }
}
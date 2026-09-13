package com.flowb2b.cotizacion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.cotizacion.dto.CotizacionRequestDTO;
import com.flowb2b.cotizacion.dto.CotizacionResponseDTO;
import com.flowb2b.cotizacion.dto.DetalleCotizacionRequestDTO;
import com.flowb2b.cotizacion.dto.DetalleCotizacionResponseDTO;
import com.flowb2b.cotizacion.entity.Cotizacion;
import com.flowb2b.cotizacion.entity.DetalleCotizacion;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.cotizacion.repository.DetalleCotizacionRepository;
import com.flowb2b.historial.service.HistorialEstadoService;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.ProductoRepository;
import com.flowb2b.solicitud.entity.Solicitud;
import com.flowb2b.solicitud.repository.SolicitudRepository;

@Service
public class CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final SolicitudRepository solicitudRepository;
    private final ProductoRepository productoRepository;
    private final HistorialEstadoService historialEstadoService;
    private final AuditoriaService auditoriaService;

    public CotizacionService(
            CotizacionRepository cotizacionRepository,
            DetalleCotizacionRepository detalleCotizacionRepository,
            SolicitudRepository solicitudRepository,
            ProductoRepository productoRepository,
            HistorialEstadoService historialEstadoService,
            AuditoriaService auditoriaService) {

        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.solicitudRepository = solicitudRepository;
        this.productoRepository = productoRepository;
        this.historialEstadoService = historialEstadoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<CotizacionResponseDTO> listar() {

        UsuarioAutenticado usuario = obtenerUsuarioAutenticado();

        List<Cotizacion> cotizaciones =
                cotizacionRepository.findByEmpresaId(
                        usuario.getEmpresaId()
                );

        List<CotizacionResponseDTO> respuesta = new ArrayList<>();

        for (Cotizacion cotizacion : cotizaciones) {
            respuesta.add(convertirAResponse(cotizacion));
        }

        return respuesta;
    }

    @Transactional(readOnly = true)
    public CotizacionResponseDTO obtenerPorId(Long idCotizacion) {

        UsuarioAutenticado usuario = obtenerUsuarioAutenticado();

        Cotizacion cotizacion =
                cotizacionRepository
                        .findByIdCotizacionAndEmpresaId(
                                idCotizacion,
                                usuario.getEmpresaId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cotización no encontrada"
                                )
                        );

        return convertirAResponse(cotizacion);
    }

    @Transactional
    public CotizacionResponseDTO crear(CotizacionRequestDTO dto) {

        UsuarioAutenticado usuario = obtenerUsuarioAutenticado();

        Long empresaId = usuario.getEmpresaId();
        Long vendedorId = usuario.getIdUsuario();

        Solicitud solicitud =
                solicitudRepository
                        .findByIdSolicitudAndEmpresaId(
                                dto.getSolicitudId(),
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Solicitud no encontrada"
                                )
                        );

        String codigo = dto.getCodigo().trim();

        if (cotizacionRepository.existsByEmpresaIdAndCodigo(
                empresaId,
                codigo)) {

            throw new IllegalArgumentException(
                    "Ya existe una cotización con ese código"
            );
        }

        validarProductos(
                dto.getDetalles(),
                empresaId
        );

        BigDecimal porcentajeDescuentoGeneral =
                dto.getPorcentajeDescuento() == null
                        ? BigDecimal.ZERO
                        : dto.getPorcentajeDescuento();

        BigDecimal subtotalGeneral = BigDecimal.ZERO;

        List<DetalleCotizacion> detallesCalculados =
                new ArrayList<>();

        for (DetalleCotizacionRequestDTO detalleDTO
                : dto.getDetalles()) {

            BigDecimal porcentajeDescuentoLinea =
                    detalleDTO.getPorcentajeDescuento() == null
                            ? BigDecimal.ZERO
                            : detalleDTO.getPorcentajeDescuento();

            BigDecimal bruto =
                    detalleDTO.getCantidad()
                            .multiply(
                                    detalleDTO.getPrecioUnitario()
                            );

            BigDecimal descuentoLinea =
                    bruto
                            .multiply(porcentajeDescuentoLinea)
                            .divide(
                                    new BigDecimal("100"),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal subtotalLinea =
                    bruto
                            .subtract(descuentoLinea)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            subtotalGeneral =
                    subtotalGeneral.add(subtotalLinea);

            DetalleCotizacion detalle =
                    new DetalleCotizacion();

            detalle.setProductoId(
                    detalleDTO.getProductoId()
            );

            detalle.setDescripcion(
                    detalleDTO.getDescripcion()
            );

            detalle.setCantidad(
                    detalleDTO.getCantidad()
            );

            detalle.setPrecioUnitario(
                    detalleDTO.getPrecioUnitario()
            );

            detalle.setPorcentajeDescuento(
                    porcentajeDescuentoLinea
            );

            detalle.setSubtotal(
                    subtotalLinea
            );

            detallesCalculados.add(detalle);
        }

        subtotalGeneral =
                subtotalGeneral.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal descuentoGeneral =
                subtotalGeneral
                        .multiply(porcentajeDescuentoGeneral)
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal baseDespuesDescuento =
                subtotalGeneral.subtract(
                        descuentoGeneral
                );

        /*
         * Por ahora dejamos el impuesto en 0.
         * Más adelante lo conectaremos con la
         * configuración de cada empresa.
         */
        BigDecimal impuesto =
                BigDecimal.ZERO.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal total =
                baseDespuesDescuento
                        .add(impuesto)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        Cotizacion cotizacion = new Cotizacion();

        cotizacion.setEmpresaId(empresaId);

        cotizacion.setSolicitudId(
                solicitud.getIdSolicitud()
        );

        cotizacion.setClienteId(
                solicitud.getClienteId()
        );

        cotizacion.setVendedorId(vendedorId);

        cotizacion.setCodigo(codigo);

        cotizacion.setSubtotal(
                subtotalGeneral
        );

        cotizacion.setDescuento(
                descuentoGeneral
        );

        cotizacion.setImpuesto(
                impuesto
        );

        cotizacion.setTotal(
                total
        );

        cotizacion.setPorcentajeDescuento(
                porcentajeDescuentoGeneral
        );

        cotizacion.setEstado(
                "BORRADOR"
        );

        cotizacion.setFechaVencimiento(
                dto.getFechaVencimiento()
        );

        cotizacion.setObservaciones(
                dto.getObservaciones()
        );

        Cotizacion cotizacionGuardada =
                cotizacionRepository.save(cotizacion);

        for (DetalleCotizacion detalle
                : detallesCalculados) {

            detalle.setCotizacionId(
                    cotizacionGuardada.getIdCotizacion()
            );

            detalleCotizacionRepository.save(detalle);
        }

        return convertirAResponse(
                cotizacionGuardada
        );
    }

    @Transactional
    public CotizacionResponseDTO aceptar(Long idCotizacion) {

        UsuarioAutenticado usuario = obtenerUsuarioAutenticado();

        Long empresaId = usuario.getEmpresaId();

        Cotizacion cotizacion =
                cotizacionRepository
                        .findByIdCotizacionAndEmpresaId(
                                idCotizacion,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cotización no encontrada"
                                )
                        );

        String estadoAnterior = cotizacion.getEstado();

        if (!"APROBADA".equalsIgnoreCase(estadoAnterior)) {
            throw new IllegalArgumentException(
                    "Solo una cotización APROBADA puede ser aceptada por el cliente"
            );
        }

        cotizacion.setEstado("ACEPTADA");
        cotizacion.setFechaAceptacion(LocalDateTime.now());

        Cotizacion actualizada =
                cotizacionRepository.save(cotizacion);

        historialEstadoService.registrar(
                "COTIZACION",
                actualizada.getIdCotizacion(),
                estadoAnterior,
                "ACEPTADA",
                "Cotización aceptada por el cliente"
        );

        auditoriaService.registrar(
                "ACEPTAR",
                "COTIZACION",
                actualizada.getIdCotizacion(),
                "{\"estado\":\"" + estadoAnterior + "\"}",
                "{\"estado\":\"ACEPTADA\","
                        + "\"fechaAceptacion\":\""
                        + actualizada.getFechaAceptacion()
                        + "\"}",
                null
        );

        return convertirAResponse(actualizada);
    }

    private void validarProductos(
            List<DetalleCotizacionRequestDTO> detalles,
            Long empresaId) {

        for (DetalleCotizacionRequestDTO detalle : detalles) {

            Producto producto =
                    productoRepository
                            .findByIdProductoAndEmpresaId(
                                    detalle.getProductoId(),
                                    empresaId
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Producto no encontrado: "
                                                    + detalle.getProductoId()
                                    )
                            );

            if (!Boolean.TRUE.equals(
                    producto.getEstado())) {

                throw new IllegalArgumentException(
                        "El producto "
                                + producto.getNombre()
                                + " se encuentra inactivo"
                );
            }
        }
    }

    private CotizacionResponseDTO convertirAResponse(
            Cotizacion cotizacion) {

        CotizacionResponseDTO response =
                new CotizacionResponseDTO();

        response.setIdCotizacion(
                cotizacion.getIdCotizacion()
        );

        response.setEmpresaId(
                cotizacion.getEmpresaId()
        );

        response.setSolicitudId(
                cotizacion.getSolicitudId()
        );

        response.setClienteId(
                cotizacion.getClienteId()
        );

        response.setVendedorId(
                cotizacion.getVendedorId()
        );

        response.setCodigo(
                cotizacion.getCodigo()
        );

        response.setSubtotal(
                cotizacion.getSubtotal()
        );

        response.setDescuento(
                cotizacion.getDescuento()
        );

        response.setImpuesto(
                cotizacion.getImpuesto()
        );

        response.setTotal(
                cotizacion.getTotal()
        );

        response.setPorcentajeDescuento(
                cotizacion.getPorcentajeDescuento()
        );

        response.setEstado(
                cotizacion.getEstado()
        );

        response.setFechaEmision(
                cotizacion.getFechaEmision()
        );

        response.setFechaVencimiento(
                cotizacion.getFechaVencimiento()
        );

        response.setFechaAceptacion(
                cotizacion.getFechaAceptacion()
        );

        response.setObservaciones(
                cotizacion.getObservaciones()
        );

        response.setFechaCreacion(
                cotizacion.getFechaCreacion()
        );

        response.setFechaActualizacion(
                cotizacion.getFechaActualizacion()
        );

        List<DetalleCotizacion> detalles =
                detalleCotizacionRepository
                        .findByCotizacionId(
                                cotizacion.getIdCotizacion()
                        );

        List<DetalleCotizacionResponseDTO> detallesResponse =
                new ArrayList<>();

        for (DetalleCotizacion detalle : detalles) {

            DetalleCotizacionResponseDTO detalleResponse =
                    new DetalleCotizacionResponseDTO();

            detalleResponse.setIdDetalle(
                    detalle.getIdDetalle()
            );

            detalleResponse.setProductoId(
                    detalle.getProductoId()
            );

            detalleResponse.setDescripcion(
                    detalle.getDescripcion()
            );

            detalleResponse.setCantidad(
                    detalle.getCantidad()
            );

            detalleResponse.setPrecioUnitario(
                    detalle.getPrecioUnitario()
            );

            detalleResponse.setPorcentajeDescuento(
                    detalle.getPorcentajeDescuento()
            );

            detalleResponse.setSubtotal(
                    detalle.getSubtotal()
            );

            detallesResponse.add(
                    detalleResponse
            );
        }

        response.setDetalles(
                detallesResponse
        );

        return response;
    }

    private UsuarioAutenticado obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException(
                    "No existe un usuario autenticado"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioAutenticado)) {
            throw new IllegalStateException(
                    "El usuario autenticado no es válido"
            );
        }

        return (UsuarioAutenticado) principal;
    }
}
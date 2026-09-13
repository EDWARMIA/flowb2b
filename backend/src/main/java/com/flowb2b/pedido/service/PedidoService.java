package com.flowb2b.pedido.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.cotizacion.entity.Cotizacion;
import com.flowb2b.cotizacion.entity.DetalleCotizacion;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.cotizacion.repository.DetalleCotizacionRepository;
import com.flowb2b.historial.service.HistorialEstadoService;
import com.flowb2b.inventario.entity.Inventario;
import com.flowb2b.inventario.repository.InventarioRepository;
import com.flowb2b.pedido.dto.PedidoDetalleResponseDTO;
import com.flowb2b.pedido.dto.PedidoEstadoRequestDTO;
import com.flowb2b.pedido.dto.PedidoRequestDTO;
import com.flowb2b.pedido.dto.PedidoResponseDTO;
import com.flowb2b.pedido.entity.DetallePedido;
import com.flowb2b.pedido.entity.Pedido;
import com.flowb2b.pedido.repository.DetallePedidoRepository;
import com.flowb2b.pedido.repository.PedidoRepository;

@Service
public class PedidoService {

    private static final Set<String>
            ESTADOS_VALIDOS =
            Set.of(
                "CREADO",
                "EN_PREPARACION",
                "DESPACHADO",
                "ENTREGADO",
                "CANCELADO"
            );

    private final PedidoRepository
            pedidoRepository;

    private final DetallePedidoRepository
            detallePedidoRepository;

    private final CotizacionRepository
            cotizacionRepository;

    private final DetalleCotizacionRepository
            detalleCotizacionRepository;

    private final InventarioRepository
            inventarioRepository;

    private final HistorialEstadoService
            historialEstadoService;

    private final AuditoriaService
            auditoriaService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            CotizacionRepository cotizacionRepository,
            DetalleCotizacionRepository detalleCotizacionRepository,
            InventarioRepository inventarioRepository,
            HistorialEstadoService historialEstadoService,
            AuditoriaService auditoriaService) {

        this.pedidoRepository =
                pedidoRepository;

        this.detallePedidoRepository =
                detallePedidoRepository;

        this.cotizacionRepository =
                cotizacionRepository;

        this.detalleCotizacionRepository =
                detalleCotizacionRepository;

        this.inventarioRepository =
                inventarioRepository;

        this.historialEstadoService =
                historialEstadoService;

        this.auditoriaService =
                auditoriaService;
    }

    // =====================================================
    // LISTAR PEDIDOS
    // =====================================================

    @Transactional(
        readOnly = true
    )
    public List<PedidoResponseDTO>
            listar() {

        Long empresaId =
                obtenerUsuarioAutenticado()
                    .getEmpresaId();

        List<Pedido> pedidos =
                pedidoRepository
                    .findByEmpresaId(
                        empresaId
                    );

        List<PedidoResponseDTO> respuesta =
                new ArrayList<>();

        for (
            Pedido pedido
            : pedidos
        ) {

            respuesta.add(
                convertirAResponse(
                    pedido
                )
            );
        }

        return respuesta;
    }

    // =====================================================
    // OBTENER PEDIDO
    // =====================================================

    @Transactional(
        readOnly = true
    )
    public PedidoResponseDTO
            obtenerPorId(
                    Long idPedido) {

        Long empresaId =
                obtenerUsuarioAutenticado()
                    .getEmpresaId();

        Pedido pedido =
                pedidoRepository
                    .findByIdPedidoAndEmpresaId(
                        idPedido,
                        empresaId
                    )
                    .orElseThrow(
                        () ->
                            new ResourceNotFoundException(
                                "Pedido no encontrado"
                            )
                    );

        return convertirAResponse(
            pedido
        );
    }

    // =====================================================
    // CREAR PEDIDO
    // =====================================================

    @Transactional
    public PedidoResponseDTO crear(
            PedidoRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Cotizacion cotizacion =
                cotizacionRepository
                    .findByIdCotizacionAndEmpresaId(
                        dto.getCotizacionId(),
                        empresaId
                    )
                    .orElseThrow(
                        () ->
                            new ResourceNotFoundException(
                                "Cotización no encontrada"
                            )
                    );

        if (
            !"ACEPTADA".equalsIgnoreCase(
                cotizacion.getEstado()
            )
        ) {

            throw new IllegalArgumentException(
                "La cotización debe estar ACEPTADA "
                + "por el cliente para generar un pedido"
            );
        }

        if (
            pedidoRepository
                .existsByCotizacionIdAndEmpresaId(
                    cotizacion.getIdCotizacion(),
                    empresaId
                )
        ) {

            throw new IllegalArgumentException(
                "La cotización ya tiene un pedido generado"
            );
        }

        if (
            pedidoRepository
                .existsByCodigoAndEmpresaId(
                    dto.getCodigo(),
                    empresaId
                )
        ) {

            throw new IllegalArgumentException(
                "Ya existe un pedido con el código "
                + dto.getCodigo()
            );
        }

        Pedido pedido =
                new Pedido();

        pedido.setEmpresaId(
            empresaId
        );

        pedido.setCotizacionId(
            cotizacion.getIdCotizacion()
        );

        pedido.setClienteId(
            cotizacion.getClienteId()
        );

        pedido.setCodigo(
            dto.getCodigo()
        );

        pedido.setEstado(
            "CREADO"
        );

        pedido.setSubtotal(
            cotizacion.getSubtotal()
        );

        pedido.setDescuento(
            cotizacion.getDescuento()
        );

        pedido.setImpuesto(
            cotizacion.getImpuesto()
        );

        pedido.setTotal(
            cotizacion.getTotal()
        );

        pedido.setFechaEstimadaEntrega(
            dto.getFechaEstimadaEntrega()
        );

        pedido.setObservaciones(
            dto.getObservaciones()
        );

        pedido =
                pedidoRepository.save(
                    pedido
                );

        List<DetalleCotizacion>
                detallesCotizacion =
                detalleCotizacionRepository
                    .findByCotizacionId(
                        cotizacion
                            .getIdCotizacion()
                    );

        if (
            detallesCotizacion.isEmpty()
        ) {

            throw new IllegalArgumentException(
                "La cotización no tiene productos"
            );
        }

        /*
         * Siempre bloqueamos inventarios
         * en el mismo orden.
         */
        detallesCotizacion.sort(
            Comparator.comparing(
                DetalleCotizacion::getProductoId
            )
        );

        for (
            DetalleCotizacion detalleCotizacion
            : detallesCotizacion
        ) {

            BigDecimal cantidad =
                    valorSeguro(
                        detalleCotizacion
                            .getCantidad()
                    );

            Inventario inventario =
                    inventarioRepository
                        .buscarPorProductoYEmpresaConBloqueo(
                            detalleCotizacion
                                .getProductoId(),
                            empresaId
                        )
                        .orElse(
                            null
                        );

            BigDecimal cantidadReservada =
                    BigDecimal.ZERO;

            if (
                inventario != null
            ) {

                BigDecimal stockActual =
                        valorSeguro(
                            inventario
                                .getStockActual()
                        );

                BigDecimal stockYaReservado =
                        valorSeguro(
                            inventario
                                .getStockReservado()
                        );

                BigDecimal stockDisponible =
                        stockActual.subtract(
                            stockYaReservado
                        );

                if (
                    stockDisponible.compareTo(
                        BigDecimal.ZERO
                    ) < 0
                ) {

                    stockDisponible =
                            BigDecimal.ZERO;
                }

                cantidadReservada =
                        cantidad.min(
                            stockDisponible
                        );

                BigDecimal nuevoStockReservado =
                        stockYaReservado.add(
                            cantidadReservada
                        );

                inventario.setStockReservado(
                    nuevoStockReservado
                );

                inventarioRepository.save(
                    inventario
                );
            }

            BigDecimal cantidadPendiente =
                    cantidad.subtract(
                        cantidadReservada
                    );

            DetallePedido detallePedido =
                    new DetallePedido();

            detallePedido.setPedidoId(
                pedido.getIdPedido()
            );

            detallePedido.setProductoId(
                detalleCotizacion
                    .getProductoId()
            );

            detallePedido.setDescripcion(
                detalleCotizacion
                    .getDescripcion()
            );

            detallePedido.setCantidad(
                cantidad
            );

            detallePedido.setPrecioUnitario(
                detalleCotizacion
                    .getPrecioUnitario()
            );

            detallePedido.setCantidadReservada(
                cantidadReservada
            );

            detallePedido.setCantidadPendiente(
                cantidadPendiente
            );

            detallePedido.setSubtotal(
                detalleCotizacion
                    .getSubtotal()
            );

            detallePedidoRepository.save(
                detallePedido
            );
        }

        historialEstadoService.registrar(
            "PEDIDO",
            pedido.getIdPedido(),
            null,
            "CREADO",
            "Pedido generado desde la cotización "
            + cotizacion.getCodigo()
        );

        auditoriaService.registrar(
            "CREAR",
            "PEDIDO",
            pedido.getIdPedido(),
            null,
            "{"
            + "\"estado\":\"CREADO\","
            + "\"codigo\":\""
            + escaparJson(
                pedido.getCodigo()
            )
            + "\","
            + "\"cotizacionId\":"
            + pedido.getCotizacionId()
            + "}",
            null
        );

        return convertirAResponse(
            pedido
        );
    }

    // =====================================================
    // REINTENTAR RESERVA DE STOCK
    // =====================================================

    @Transactional
    public PedidoResponseDTO
            reintentarReserva(
                    Long idPedido) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Pedido pedido =
                pedidoRepository
                    .findByIdPedidoAndEmpresaId(
                        idPedido,
                        empresaId
                    )
                    .orElseThrow(
                        () ->
                            new ResourceNotFoundException(
                                "Pedido no encontrado"
                            )
                    );

        /*
         * Solo tiene sentido volver a reservar
         * mientras el pedido todavía no fue
         * despachado, entregado ni cancelado.
         */
        if (
            !"CREADO".equals(
                pedido.getEstado()
            )
            &&
            !"EN_PREPARACION".equals(
                pedido.getEstado()
            )
        ) {

            throw new IllegalArgumentException(
                "Solo se puede reintentar la reserva "
                + "cuando el pedido está CREADO "
                + "o EN_PREPARACION"
            );
        }

        List<DetallePedido> detalles =
                detallePedidoRepository
                    .findByPedidoId(
                        pedido.getIdPedido()
                    );

        if (
            detalles.isEmpty()
        ) {

            throw new IllegalArgumentException(
                "El pedido no tiene productos"
            );
        }

        /*
         * Mismo orden de bloqueo que usamos
         * al crear, despachar y cancelar.
         */
        detalles.sort(
            Comparator.comparing(
                DetallePedido::getProductoId
            )
        );

        BigDecimal totalReservadoAhora =
                BigDecimal.ZERO;

        for (
            DetallePedido detalle
            : detalles
        ) {

            BigDecimal pendiente =
                    valorSeguro(
                        detalle
                            .getCantidadPendiente()
                    );

            /*
             * Este producto ya está
             * completamente reservado.
             */
            if (
                pendiente.compareTo(
                    BigDecimal.ZERO
                ) <= 0
            ) {

                continue;
            }

            Inventario inventario =
                    inventarioRepository
                        .buscarPorProductoYEmpresaConBloqueo(
                            detalle
                                .getProductoId(),
                            empresaId
                        )
                        .orElse(
                            null
                        );

            /*
             * Si no existe inventario,
             * simplemente sigue pendiente.
             */
            if (
                inventario == null
            ) {

                continue;
            }

            BigDecimal stockActual =
                    valorSeguro(
                        inventario
                            .getStockActual()
                    );

            BigDecimal stockReservado =
                    valorSeguro(
                        inventario
                            .getStockReservado()
                    );

            BigDecimal disponible =
                    stockActual.subtract(
                        stockReservado
                    );

            if (
                disponible.compareTo(
                    BigDecimal.ZERO
                ) <= 0
            ) {

                continue;
            }

            /*
             * Reservamos como máximo:
             *
             * - lo que aún necesita el pedido
             * - lo que realmente está disponible
             */
            BigDecimal reservaNueva =
                    pendiente.min(
                        disponible
                    );

            if (
                reservaNueva.compareTo(
                    BigDecimal.ZERO
                ) <= 0
            ) {

                continue;
            }

            String datosInventarioAntes =
                    construirJsonInventario(
                        inventario
                    );

            BigDecimal reservadoPedidoActual =
                    valorSeguro(
                        detalle
                            .getCantidadReservada()
                    );

            BigDecimal nuevoReservadoPedido =
                    reservadoPedidoActual.add(
                        reservaNueva
                    );

            BigDecimal nuevoPendiente =
                    pendiente.subtract(
                        reservaNueva
                    );

            BigDecimal nuevoReservadoInventario =
                    stockReservado.add(
                        reservaNueva
                    );

            detalle.setCantidadReservada(
                nuevoReservadoPedido
            );

            detalle.setCantidadPendiente(
                nuevoPendiente
            );

            inventario.setStockReservado(
                nuevoReservadoInventario
            );

            detallePedidoRepository.save(
                detalle
            );

            Inventario actualizado =
                    inventarioRepository.save(
                        inventario
                    );

            String datosInventarioDespues =
                    construirJsonInventario(
                        actualizado
                    );

            auditoriaService.registrar(
                "RESERVAR_STOCK_PENDIENTE",
                "INVENTARIO",
                actualizado
                    .getIdInventario(),
                datosInventarioAntes,
                datosInventarioDespues,
                null
            );

            totalReservadoAhora =
                    totalReservadoAhora.add(
                        reservaNueva
                    );
        }

        /*
         * No cambia el estado del pedido.
         * Solo registramos la operación.
         */
        auditoriaService.registrar(
            "REINTENTAR_RESERVA",
            "PEDIDO",
            pedido.getIdPedido(),
            null,
            "{"
            + "\"cantidadReservadaAhora\":"
            + totalReservadoAhora
            + "}",
            null
        );

        return convertirAResponse(
            pedido
        );
    }

    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================

    @Transactional
    public PedidoResponseDTO cambiarEstado(
            Long idPedido,
            PedidoEstadoRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Pedido pedido =
                pedidoRepository
                    .findByIdPedidoAndEmpresaId(
                        idPedido,
                        empresaId
                    )
                    .orElseThrow(
                        () ->
                            new ResourceNotFoundException(
                                "Pedido no encontrado"
                            )
                    );

        String estadoAnterior =
                pedido.getEstado();

        String estadoNuevo =
                dto.getEstado()
                    .trim()
                    .toUpperCase();

        if (
            !ESTADOS_VALIDOS.contains(
                estadoNuevo
            )
        ) {

            throw new IllegalArgumentException(
                "Estado inválido. Valores permitidos: "
                + "CREADO, EN_PREPARACION, "
                + "DESPACHADO, ENTREGADO, CANCELADO"
            );
        }

        if (
            estadoNuevo.equalsIgnoreCase(
                estadoAnterior
            )
        ) {

            throw new IllegalArgumentException(
                "El pedido ya se encuentra en estado "
                + estadoNuevo
            );
        }

        validarTransicionEstado(
            estadoAnterior,
            estadoNuevo
        );

        /*
         * Primero procesamos inventario.
         * Si falla algo, @Transactional
         * revierte toda la operación.
         */
        if (
            "DESPACHADO".equals(
                estadoNuevo
            )
        ) {

            procesarInventarioDespacho(
                pedido,
                empresaId
            );
        }

        if (
            "CANCELADO".equals(
                estadoNuevo
            )
        ) {

            procesarInventarioCancelacion(
                pedido,
                empresaId
            );
        }

        pedido.setEstado(
            estadoNuevo
        );

        if (
            "ENTREGADO".equals(
                estadoNuevo
            )
        ) {

            pedido.setFechaEntrega(
                LocalDateTime.now()
            );
        }

        Pedido actualizado =
                pedidoRepository.save(
                    pedido
                );

        String comentario =
                dto.getComentario();

        if (
            comentario == null
            ||
            comentario.isBlank()
        ) {

            comentario =
                    "Cambio de estado del pedido";
        }

        historialEstadoService.registrar(
            "PEDIDO",
            actualizado.getIdPedido(),
            estadoAnterior,
            estadoNuevo,
            comentario
        );

        auditoriaService.registrar(
            "CAMBIAR_ESTADO",
            "PEDIDO",
            actualizado.getIdPedido(),
            "{"
            + "\"estado\":\""
            + escaparJson(
                estadoAnterior
            )
            + "\"}",
            "{"
            + "\"estado\":\""
            + escaparJson(
                estadoNuevo
            )
            + "\"}",
            null
        );

        return convertirAResponse(
            actualizado
        );
    }

    // =====================================================
    // PROCESAR DESPACHO
    // =====================================================

    private void procesarInventarioDespacho(
            Pedido pedido,
            Long empresaId) {

        List<DetallePedido> detalles =
                detallePedidoRepository
                    .findByPedidoId(
                        pedido.getIdPedido()
                    );

        if (
            detalles.isEmpty()
        ) {

            throw new IllegalArgumentException(
                "El pedido no tiene productos"
            );
        }

        detalles.sort(
            Comparator.comparing(
                DetallePedido::getProductoId
            )
        );

        /*
         * PRIMER RECORRIDO:
         * validamos todos los productos
         * antes de modificar el inventario.
         */
        for (
            DetallePedido detalle
            : detalles
        ) {

            BigDecimal pendiente =
                    valorSeguro(
                        detalle
                            .getCantidadPendiente()
                    );

            if (
                pendiente.compareTo(
                    BigDecimal.ZERO
                ) > 0
            ) {

                throw new IllegalArgumentException(
                    "No se puede despachar el pedido. "
                    + "El producto "
                    + detalle.getProductoId()
                    + " tiene "
                    + pendiente
                    + " unidades pendientes"
                );
            }

            BigDecimal cantidadReservada =
                    valorSeguro(
                        detalle
                            .getCantidadReservada()
                    );

            if (
                cantidadReservada.compareTo(
                    BigDecimal.ZERO
                ) <= 0
            ) {

                throw new IllegalArgumentException(
                    "El producto "
                    + detalle.getProductoId()
                    + " no tiene stock reservado "
                    + "para despachar"
                );
            }

            Inventario inventario =
                    inventarioRepository
                        .buscarPorProductoYEmpresaConBloqueo(
                            detalle
                                .getProductoId(),
                            empresaId
                        )
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Inventario no encontrado "
                                    + "para el producto "
                                    + detalle.getProductoId()
                                )
                        );

            BigDecimal stockActual =
                    valorSeguro(
                        inventario
                            .getStockActual()
                    );

            BigDecimal stockReservado =
                    valorSeguro(
                        inventario
                            .getStockReservado()
                    );

            if (
                stockActual.compareTo(
                    cantidadReservada
                ) < 0
            ) {

                throw new IllegalArgumentException(
                    "Stock actual insuficiente para despachar "
                    + "el producto "
                    + detalle.getProductoId()
                );
            }

            if (
                stockReservado.compareTo(
                    cantidadReservada
                ) < 0
            ) {

                throw new IllegalArgumentException(
                    "El stock reservado del producto "
                    + detalle.getProductoId()
                    + " es menor que la cantidad "
                    + "reservada por el pedido"
                );
            }
        }

        /*
         * SEGUNDO RECORRIDO:
         * después de validar todo,
         * realizamos los movimientos.
         */
        for (
            DetallePedido detalle
            : detalles
        ) {

            Inventario inventario =
                    inventarioRepository
                        .buscarPorProductoYEmpresaConBloqueo(
                            detalle
                                .getProductoId(),
                            empresaId
                        )
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Inventario no encontrado "
                                    + "para el producto "
                                    + detalle.getProductoId()
                                )
                        );

            BigDecimal cantidadReservada =
                    valorSeguro(
                        detalle
                            .getCantidadReservada()
                    );

            String datosAnteriores =
                    construirJsonInventario(
                        inventario
                    );

            BigDecimal nuevoStockActual =
                    valorSeguro(
                        inventario
                            .getStockActual()
                    )
                    .subtract(
                        cantidadReservada
                    );

            BigDecimal nuevoStockReservado =
                    valorSeguro(
                        inventario
                            .getStockReservado()
                    )
                    .subtract(
                        cantidadReservada
                    );

            inventario.setStockActual(
                nuevoStockActual
            );

            inventario.setStockReservado(
                nuevoStockReservado
            );

            Inventario actualizado =
                    inventarioRepository.save(
                        inventario
                    );

            String datosNuevos =
                    construirJsonInventario(
                        actualizado
                    );

            auditoriaService.registrar(
                "DESPACHAR_PEDIDO",
                "INVENTARIO",
                actualizado
                    .getIdInventario(),
                datosAnteriores,
                datosNuevos,
                null
            );
        }
    }

    // =====================================================
    // PROCESAR CANCELACIÓN
    // =====================================================

    private void procesarInventarioCancelacion(
            Pedido pedido,
            Long empresaId) {

        List<DetallePedido> detalles =
                detallePedidoRepository
                    .findByPedidoId(
                        pedido.getIdPedido()
                    );

        if (
            detalles.isEmpty()
        ) {

            throw new IllegalArgumentException(
                "El pedido no tiene productos"
            );
        }

        detalles.sort(
            Comparator.comparing(
                DetallePedido::getProductoId
            )
        );

        for (
            DetallePedido detalle
            : detalles
        ) {

            BigDecimal cantidadReservada =
                    valorSeguro(
                        detalle
                            .getCantidadReservada()
                    );

            if (
                cantidadReservada.compareTo(
                    BigDecimal.ZERO
                ) <= 0
            ) {

                continue;
            }

            Inventario inventario =
                    inventarioRepository
                        .buscarPorProductoYEmpresaConBloqueo(
                            detalle
                                .getProductoId(),
                            empresaId
                        )
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    "Inventario no encontrado "
                                    + "para el producto "
                                    + detalle.getProductoId()
                                )
                        );

            BigDecimal stockReservado =
                    valorSeguro(
                        inventario
                            .getStockReservado()
                    );

            if (
                stockReservado.compareTo(
                    cantidadReservada
                ) < 0
            ) {

                throw new IllegalArgumentException(
                    "El stock reservado del producto "
                    + detalle.getProductoId()
                    + " es menor que la reserva "
                    + "del pedido"
                );
            }

            String datosAnteriores =
                    construirJsonInventario(
                        inventario
                    );

            BigDecimal nuevoStockReservado =
                    stockReservado.subtract(
                        cantidadReservada
                    );

            /*
             * Al cancelar:
             *
             * stockActual no cambia.
             * Solamente liberamos reserva.
             */
            inventario.setStockReservado(
                nuevoStockReservado
            );

            Inventario actualizado =
                    inventarioRepository.save(
                        inventario
                    );

            String datosNuevos =
                    construirJsonInventario(
                        actualizado
                    );

            auditoriaService.registrar(
                "CANCELAR_PEDIDO",
                "INVENTARIO",
                actualizado
                    .getIdInventario(),
                datosAnteriores,
                datosNuevos,
                null
            );
        }
    }

    // =====================================================
    // TRANSICIONES DE ESTADO
    // =====================================================

    private void validarTransicionEstado(
            String estadoAnterior,
            String estadoNuevo) {

        boolean transicionValida =
                false;

        if (
            "CREADO".equals(
                estadoAnterior
            )
        ) {

            transicionValida =
                    "EN_PREPARACION".equals(
                        estadoNuevo
                    )
                    ||
                    "CANCELADO".equals(
                        estadoNuevo
                    );

        } else if (
            "EN_PREPARACION".equals(
                estadoAnterior
            )
        ) {

            transicionValida =
                    "DESPACHADO".equals(
                        estadoNuevo
                    )
                    ||
                    "CANCELADO".equals(
                        estadoNuevo
                    );

        } else if (
            "DESPACHADO".equals(
                estadoAnterior
            )
        ) {

            transicionValida =
                    "ENTREGADO".equals(
                        estadoNuevo
                    );
        }

        if (
            !transicionValida
        ) {

            throw new IllegalArgumentException(
                "No se permite cambiar el pedido de "
                + estadoAnterior
                + " a "
                + estadoNuevo
            );
        }
    }

    // =====================================================
    // JSON INVENTARIO PARA AUDITORÍA
    // =====================================================

    private String construirJsonInventario(
            Inventario inventario) {

        BigDecimal stockActual =
                valorSeguro(
                    inventario
                        .getStockActual()
                );

        BigDecimal stockReservado =
                valorSeguro(
                    inventario
                        .getStockReservado()
                );

        BigDecimal stockMinimo =
                valorSeguro(
                    inventario
                        .getStockMinimo()
                );

        BigDecimal stockDisponible =
                stockActual.subtract(
                    stockReservado
                );

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
            + ","
            + "\"origen\":\"PEDIDO\""
            + "}";
    }

    // =====================================================
    // ENTITY -> RESPONSE
    // =====================================================

    private PedidoResponseDTO convertirAResponse(
            Pedido pedido) {

        PedidoResponseDTO response =
                new PedidoResponseDTO();

        response.setIdPedido(
            pedido.getIdPedido()
        );

        response.setEmpresaId(
            pedido.getEmpresaId()
        );

        response.setCotizacionId(
            pedido.getCotizacionId()
        );

        response.setClienteId(
            pedido.getClienteId()
        );

        response.setCodigo(
            pedido.getCodigo()
        );

        response.setEstado(
            pedido.getEstado()
        );

        response.setSubtotal(
            pedido.getSubtotal()
        );

        response.setDescuento(
            pedido.getDescuento()
        );

        response.setImpuesto(
            pedido.getImpuesto()
        );

        response.setTotal(
            pedido.getTotal()
        );

        response.setFechaPedido(
            pedido.getFechaPedido()
        );

        response.setFechaEstimadaEntrega(
            pedido
                .getFechaEstimadaEntrega()
        );

        response.setFechaEntrega(
            pedido.getFechaEntrega()
        );

        response.setObservaciones(
            pedido.getObservaciones()
        );

        response.setFechaCreacion(
            pedido.getFechaCreacion()
        );

        response.setFechaActualizacion(
            pedido.getFechaActualizacion()
        );

        List<DetallePedido> detalles =
                detallePedidoRepository
                    .findByPedidoId(
                        pedido.getIdPedido()
                    );

        List<PedidoDetalleResponseDTO>
                detallesResponse =
                new ArrayList<>();

        for (
            DetallePedido detalle
            : detalles
        ) {

            PedidoDetalleResponseDTO
                    detalleResponse =
                    new PedidoDetalleResponseDTO();

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

            detalleResponse.setCantidadReservada(
                detalle.getCantidadReservada()
            );

            detalleResponse.setCantidadPendiente(
                detalle.getCantidadPendiente()
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

    // =====================================================
    // BIGDECIMAL SEGURO
    // =====================================================

    private BigDecimal valorSeguro(
            BigDecimal valor) {

        if (
            valor == null
        ) {

            return BigDecimal.ZERO;
        }

        return valor;
    }

    // =====================================================
    // ESCAPAR JSON
    // =====================================================

    private String escaparJson(
            String valor) {

        if (
            valor == null
        ) {

            return "";
        }

        return valor
            .replace(
                "\\",
                "\\\\"
            )
            .replace(
                "\"",
                "\\\""
            )
            .replace(
                "\n",
                "\\n"
            )
            .replace(
                "\r",
                "\\r"
            )
            .replace(
                "\t",
                "\\t"
            );
    }

    // =====================================================
    // USUARIO AUTENTICADO
    // =====================================================

    private UsuarioAutenticado
            obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                    .getContext()
                    .getAuthentication();

        if (
            authentication == null
            ||
            !authentication.isAuthenticated()
        ) {

            throw new IllegalStateException(
                "Usuario no autenticado"
            );
        }

        Object principal =
                authentication
                    .getPrincipal();

        if (
            !(principal
                instanceof UsuarioAutenticado)
        ) {

            throw new IllegalStateException(
                "No se pudo obtener "
                + "el usuario autenticado"
            );
        }

        return (
            UsuarioAutenticado
        ) principal;
    }
}
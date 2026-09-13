package com.flowb2b.DashboardGerencialService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialAnaliticaDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialCotizacionEstadoDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialPedidoMensualDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialProductoDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialResumenDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialVentaMensualDTO;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.cotizacion.entity.Cotizacion;
import com.flowb2b.cotizacion.entity.DetalleCotizacion;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.cotizacion.repository.DetalleCotizacionRepository;
import com.flowb2b.pedido.entity.DetallePedido;
import com.flowb2b.pedido.entity.Pedido;
import com.flowb2b.pedido.repository.DetallePedidoRepository;
import com.flowb2b.pedido.repository.PedidoRepository;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.ProductoRepository;

@Service
public class DashboardGerencialService {

    private final PedidoRepository
            pedidoRepository;

    private final DetallePedidoRepository
            detallePedidoRepository;

    private final CotizacionRepository
            cotizacionRepository;

    private final DetalleCotizacionRepository
            detalleCotizacionRepository;

    private final ProductoRepository
            productoRepository;


    public DashboardGerencialService(
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            CotizacionRepository cotizacionRepository,
            DetalleCotizacionRepository detalleCotizacionRepository,
            ProductoRepository productoRepository) {

        this.pedidoRepository =
                pedidoRepository;

        this.detallePedidoRepository =
                detallePedidoRepository;

        this.cotizacionRepository =
                cotizacionRepository;

        this.detalleCotizacionRepository =
                detalleCotizacionRepository;

        this.productoRepository =
                productoRepository;
    }


    // =====================================================
    // RESUMEN SIN FILTROS
    // =====================================================

    @Transactional(readOnly = true)
    public DashboardGerencialResumenDTO
            obtenerResumen() {

        return obtenerAnalitica(
                null,
                null,
                null,
                null,
                null
        ).getResumen();
    }


    // =====================================================
    // VENTAS MENSUALES SIN FILTROS
    // =====================================================

    @Transactional(readOnly = true)
    public List<DashboardGerencialVentaMensualDTO>
            obtenerVentasMensuales() {

        return obtenerAnalitica(
                null,
                null,
                null,
                null,
                null
        ).getVentasMensuales();
    }


    // =====================================================
    // ANALÍTICA GENERAL
    // =====================================================

    @Transactional(readOnly = true)
    public DashboardGerencialAnaliticaDTO
            obtenerAnalitica(
                    LocalDate desde,
                    LocalDate hasta,
                    Long clienteId,
                    Long productoId,
                    Long vendedorId) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();


        List<Pedido> pedidosEmpresa =
                pedidoRepository
                        .findByEmpresaId(
                                empresaId
                        );


        List<Cotizacion> cotizacionesEmpresa =
                cotizacionRepository
                        .findByEmpresaId(
                                empresaId
                        );


        // =================================================
        // FILTRAR PEDIDOS
        // =================================================

        List<Pedido> pedidosFiltrados =
                new ArrayList<>();


        for (
                Pedido pedido
                : pedidosEmpresa
        ) {

            if (
                    !pedidoCuentaComoVenta(
                            pedido
                    )
            ) {
                continue;
            }


            if (
                    !cumpleFechaPedido(
                            pedido,
                            desde,
                            hasta
                    )
            ) {
                continue;
            }


            if (
                    clienteId != null &&
                    !clienteId.equals(
                            pedido.getClienteId()
                    )
            ) {
                continue;
            }


            if (
                    productoId != null &&
                    !pedidoContieneProducto(
                            pedido,
                            productoId
                    )
            ) {
                continue;
            }


            if (
                    vendedorId != null &&
                    !pedidoPerteneceVendedor(
                            pedido,
                            vendedorId,
                            empresaId
                    )
            ) {
                continue;
            }


            pedidosFiltrados.add(
                    pedido
            );
        }


        // =================================================
        // FILTRAR COTIZACIONES
        // =================================================

        List<Cotizacion> cotizacionesFiltradas =
                new ArrayList<>();


        for (
                Cotizacion cotizacion
                : cotizacionesEmpresa
        ) {

            if (
                    !cumpleFechaCotizacion(
                            cotizacion,
                            desde,
                            hasta
                    )
            ) {
                continue;
            }


            if (
                    clienteId != null &&
                    !clienteId.equals(
                            cotizacion.getClienteId()
                    )
            ) {
                continue;
            }


            if (
                    vendedorId != null &&
                    !vendedorId.equals(
                            cotizacion.getVendedorId()
                    )
            ) {
                continue;
            }


            if (
                    productoId != null &&
                    !cotizacionContieneProducto(
                            cotizacion,
                            productoId
                    )
            ) {
                continue;
            }


            cotizacionesFiltradas.add(
                    cotizacion
            );
        }


        DashboardGerencialAnaliticaDTO
                response =
                new DashboardGerencialAnaliticaDTO();


        response.setResumen(
                construirResumen(
                        pedidosFiltrados,
                        cotizacionesFiltradas
                )
        );


        response.setVentasMensuales(
                construirVentasMensuales(
                        pedidosFiltrados,
                        desde,
                        hasta
                )
        );


        response.setPedidosMensuales(
                construirPedidosMensuales(
                        pedidosFiltrados,
                        desde,
                        hasta
                )
        );


        response.setTopProductos(
                construirTopProductos(
                        pedidosFiltrados,
                        empresaId
                )
        );


        response.setCotizacionesPorEstado(
                construirCotizacionesPorEstado(
                        cotizacionesFiltradas
                )
        );


        return response;
    }


    // =====================================================
    // RESUMEN
    // =====================================================

    private DashboardGerencialResumenDTO
            construirResumen(
                    List<Pedido> pedidos,
                    List<Cotizacion> cotizaciones) {

        BigDecimal ventasTotales =
                BigDecimal.ZERO;


        for (
                Pedido pedido
                : pedidos
        ) {

            if (
                    pedido.getTotal()
                            != null
            ) {

                ventasTotales =
                        ventasTotales.add(
                                pedido.getTotal()
                        );
            }
        }


        ventasTotales =
                ventasTotales.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        long totalPedidos =
                pedidos.size();


        long totalCotizaciones =
                cotizaciones.size();


        BigDecimal ticketPromedio =
                BigDecimal.ZERO;


        if (
                totalPedidos > 0
        ) {

            ticketPromedio =
                    ventasTotales.divide(
                            BigDecimal.valueOf(
                                    totalPedidos
                            ),
                            2,
                            RoundingMode.HALF_UP
                    );
        }


        BigDecimal tasaConversion =
                BigDecimal.ZERO;


        if (
                totalCotizaciones > 0
        ) {

            tasaConversion =
                    BigDecimal
                            .valueOf(
                                    totalPedidos
                            )
                            .multiply(
                                    new BigDecimal(
                                            "100"
                                    )
                            )
                            .divide(
                                    BigDecimal.valueOf(
                                            totalCotizaciones
                                    ),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }


        DashboardGerencialResumenDTO dto =
                new DashboardGerencialResumenDTO();


        dto.setVentasTotales(
                ventasTotales
        );

        dto.setTotalPedidos(
                totalPedidos
        );

        dto.setTicketPromedio(
                ticketPromedio
        );

        dto.setTasaConversion(
                tasaConversion
        );

        dto.setTotalCotizaciones(
                totalCotizaciones
        );


        return dto;
    }


    // =====================================================
    // VENTAS MENSUALES
    // =====================================================

    private List<DashboardGerencialVentaMensualDTO>
            construirVentasMensuales(
                    List<Pedido> pedidos,
                    LocalDate desde,
                    LocalDate hasta) {

        List<YearMonth> periodos =
                obtenerPeriodos(
                        pedidos,
                        desde,
                        hasta
                );


        List<DashboardGerencialVentaMensualDTO>
                resultado =
                new ArrayList<>();


        for (
                YearMonth periodo
                : periodos
        ) {

            BigDecimal ventas =
                    BigDecimal.ZERO;

            long cantidadPedidos =
                    0;


            for (
                    Pedido pedido
                    : pedidos
            ) {

                LocalDateTime fecha =
                        obtenerFechaPedido(
                                pedido
                        );


                if (
                        fecha == null
                ) {
                    continue;
                }


                if (
                        !periodo.equals(
                                YearMonth.from(
                                        fecha
                                )
                        )
                ) {
                    continue;
                }


                cantidadPedidos++;


                if (
                        pedido.getTotal()
                                != null
                ) {

                    ventas =
                            ventas.add(
                                    pedido.getTotal()
                            );
                }
            }


            DashboardGerencialVentaMensualDTO dto =
                    new DashboardGerencialVentaMensualDTO();


            dto.setPeriodo(
                    periodo.toString()
            );

            dto.setMes(
                    obtenerNombreMes(
                            periodo.getMonthValue()
                    )
            );

            dto.setVentas(
                    ventas.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );

            dto.setPedidos(
                    cantidadPedidos
            );


            resultado.add(
                    dto
            );
        }


        return resultado;
    }


    // =====================================================
    // PEDIDOS MENSUALES
    // =====================================================

    private List<DashboardGerencialPedidoMensualDTO>
            construirPedidosMensuales(
                    List<Pedido> pedidos,
                    LocalDate desde,
                    LocalDate hasta) {

        List<YearMonth> periodos =
                obtenerPeriodos(
                        pedidos,
                        desde,
                        hasta
                );


        List<DashboardGerencialPedidoMensualDTO>
                resultado =
                new ArrayList<>();


        for (
                YearMonth periodo
                : periodos
        ) {

            long cantidad =
                    0;


            for (
                    Pedido pedido
                    : pedidos
            ) {

                LocalDateTime fecha =
                        obtenerFechaPedido(
                                pedido
                        );


                if (
                        fecha != null &&
                        periodo.equals(
                                YearMonth.from(
                                        fecha
                                )
                        )
                ) {

                    cantidad++;
                }
            }


            DashboardGerencialPedidoMensualDTO dto =
                    new DashboardGerencialPedidoMensualDTO();


            dto.setPeriodo(
                    periodo.toString()
            );

            dto.setMes(
                    obtenerNombreMes(
                            periodo.getMonthValue()
                    )
            );

            dto.setPedidos(
                    cantidad
            );


            resultado.add(
                    dto
            );
        }


        return resultado;
    }


    // =====================================================
    // TOP 5 PRODUCTOS
    // =====================================================

    private List<DashboardGerencialProductoDTO>
            construirTopProductos(
                    List<Pedido> pedidos,
                    Long empresaId) {

        Map<Long, BigDecimal>
                cantidades =
                new HashMap<>();

        Map<Long, BigDecimal>
                ventas =
                new HashMap<>();


        for (
                Pedido pedido
                : pedidos
        ) {

            List<DetallePedido> detalles =
                    detallePedidoRepository
                            .findByPedidoId(
                                    pedido.getIdPedido()
                            );


            for (
                    DetallePedido detalle
                    : detalles
            ) {

                Long productoId =
                        detalle.getProductoId();


                if (
                        productoId == null
                ) {
                    continue;
                }


                BigDecimal cantidad =
                        detalle.getCantidad() == null
                                ? BigDecimal.ZERO
                                : detalle.getCantidad();


                BigDecimal subtotal =
                        detalle.getSubtotal() == null
                                ? BigDecimal.ZERO
                                : detalle.getSubtotal();


                cantidades.merge(
                        productoId,
                        cantidad,
                        BigDecimal::add
                );


                ventas.merge(
                        productoId,
                        subtotal,
                        BigDecimal::add
                );
            }
        }


        List<Long> productoIds =
                new ArrayList<>(
                        cantidades.keySet()
                );


        productoIds.sort(
                Comparator.comparing(
                        id ->
                                cantidades.get(
                                        id
                                )
                ).reversed()
        );


        List<DashboardGerencialProductoDTO>
                resultado =
                new ArrayList<>();


        int limite =
                Math.min(
                        5,
                        productoIds.size()
                );


        for (
                int i = 0;
                i < limite;
                i++
        ) {

            Long productoId =
                    productoIds.get(
                            i
                    );


            Producto producto =
                    productoRepository
                            .findByIdProductoAndEmpresaId(
                                    productoId,
                                    empresaId
                            )
                            .orElse(
                                    null
                            );


            DashboardGerencialProductoDTO dto =
                    new DashboardGerencialProductoDTO();


            dto.setProductoId(
                    productoId
            );


            dto.setProducto(
                    producto != null
                            ? producto.getNombre()
                            : "Producto #"
                            + productoId
            );


            dto.setCantidadVendida(
                    cantidades.get(
                            productoId
                    )
            );


            dto.setVentas(
                    ventas
                            .getOrDefault(
                                    productoId,
                                    BigDecimal.ZERO
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            )
            );


            resultado.add(
                    dto
            );
        }


        return resultado;
    }


    // =====================================================
    // COTIZACIONES POR ESTADO
    // =====================================================

    private List<DashboardGerencialCotizacionEstadoDTO>
            construirCotizacionesPorEstado(
                    List<Cotizacion> cotizaciones) {

        Map<String, Long> cantidades =
                new LinkedHashMap<>();


        for (
                Cotizacion cotizacion
                : cotizaciones
        ) {

            String estado =
                    cotizacion.getEstado();


            if (
                    estado == null ||
                    estado.isBlank()
            ) {

                estado =
                        "SIN_ESTADO";
            }


            cantidades.merge(
                    estado,
                    1L,
                    Long::sum
            );
        }


        List<DashboardGerencialCotizacionEstadoDTO>
                resultado =
                new ArrayList<>();


        for (
                Map.Entry<String, Long> entry
                : cantidades.entrySet()
        ) {

            DashboardGerencialCotizacionEstadoDTO dto =
                    new DashboardGerencialCotizacionEstadoDTO();


            dto.setEstado(
                    entry.getKey()
            );

            dto.setCantidad(
                    entry.getValue()
            );


            resultado.add(
                    dto
            );
        }


        resultado.sort(
                Comparator.comparing(
                        DashboardGerencialCotizacionEstadoDTO
                                ::getCantidad
                ).reversed()
        );


        return resultado;
    }


    // =====================================================
    // PERÍODOS PARA LOS GRÁFICOS
    // =====================================================

    private List<YearMonth> obtenerPeriodos(
            List<Pedido> pedidos,
            LocalDate desde,
            LocalDate hasta) {

        YearMonth fin;


        if (
                hasta != null
        ) {

            fin =
                    YearMonth.from(
                            hasta
                    );

        } else {

            fin =
                    null;


            for (
                    Pedido pedido
                    : pedidos
            ) {

                LocalDateTime fecha =
                        obtenerFechaPedido(
                                pedido
                        );


                if (
                        fecha == null
                ) {
                    continue;
                }


                YearMonth periodo =
                        YearMonth.from(
                                fecha
                        );


                if (
                        fin == null ||
                        periodo.isAfter(
                                fin
                        )
                ) {

                    fin =
                            periodo;
                }
            }


            if (
                    fin == null
            ) {

                fin =
                        YearMonth.now();
            }
        }


        YearMonth inicio;


        if (
                desde != null
        ) {

            inicio =
                    YearMonth.from(
                            desde
                    );

        } else {

            inicio =
                    fin.minusMonths(
                            5
                    );
        }


        // Para evitar gráficos gigantes,
        // mostramos máximo 12 meses.

        if (
                inicio.isBefore(
                        fin.minusMonths(
                                11
                        )
                )
        ) {

            inicio =
                    fin.minusMonths(
                            11
                    );
        }


        List<YearMonth> resultado =
                new ArrayList<>();


        YearMonth actual =
                inicio;


        while (
                !actual.isAfter(
                        fin
                )
        ) {

            resultado.add(
                    actual
            );

            actual =
                    actual.plusMonths(
                            1
                    );
        }


        return resultado;
    }


    // =====================================================
    // FILTROS
    // =====================================================

    private boolean cumpleFechaPedido(
            Pedido pedido,
            LocalDate desde,
            LocalDate hasta) {

        LocalDateTime fecha =
                obtenerFechaPedido(
                        pedido
                );


        if (
                fecha == null
        ) {

            return desde == null &&
                    hasta == null;
        }


        LocalDate fechaPedido =
                fecha.toLocalDate();


        if (
                desde != null &&
                fechaPedido.isBefore(
                        desde
                )
        ) {

            return false;
        }


        if (
                hasta != null &&
                fechaPedido.isAfter(
                        hasta
                )
        ) {

            return false;
        }


        return true;
    }


    private boolean cumpleFechaCotizacion(
            Cotizacion cotizacion,
            LocalDate desde,
            LocalDate hasta) {

        LocalDateTime fecha =
                obtenerFechaCotizacion(
                        cotizacion
                );


        if (
                fecha == null
        ) {

            return desde == null &&
                    hasta == null;
        }


        LocalDate fechaCotizacion =
                fecha.toLocalDate();


        if (
                desde != null &&
                fechaCotizacion.isBefore(
                        desde
                )
        ) {

            return false;
        }


        if (
                hasta != null &&
                fechaCotizacion.isAfter(
                        hasta
                )
        ) {

            return false;
        }


        return true;
    }


    private boolean pedidoContieneProducto(
            Pedido pedido,
            Long productoId) {

        List<DetallePedido> detalles =
                detallePedidoRepository
                        .findByPedidoId(
                                pedido.getIdPedido()
                        );


        for (
                DetallePedido detalle
                : detalles
        ) {

            if (
                    productoId.equals(
                            detalle.getProductoId()
                    )
            ) {

                return true;
            }
        }


        return false;
    }


    private boolean cotizacionContieneProducto(
            Cotizacion cotizacion,
            Long productoId) {

        List<DetalleCotizacion> detalles =
                detalleCotizacionRepository
                        .findByCotizacionId(
                                cotizacion
                                        .getIdCotizacion()
                        );


        for (
                DetalleCotizacion detalle
                : detalles
        ) {

            if (
                    productoId.equals(
                            detalle.getProductoId()
                    )
            ) {

                return true;
            }
        }


        return false;
    }


    private boolean pedidoPerteneceVendedor(
            Pedido pedido,
            Long vendedorId,
            Long empresaId) {

        if (
                pedido.getCotizacionId()
                        == null
        ) {

            return false;
        }


        Cotizacion cotizacion =
                cotizacionRepository
                        .findByIdCotizacionAndEmpresaId(
                                pedido.getCotizacionId(),
                                empresaId
                        )
                        .orElse(
                                null
                        );


        return cotizacion != null &&
                vendedorId.equals(
                        cotizacion.getVendedorId()
                );
    }


    // =====================================================
    // FECHAS
    // =====================================================

    private LocalDateTime obtenerFechaPedido(
            Pedido pedido) {

        if (
                pedido.getFechaPedido()
                        != null
        ) {

            return pedido
                    .getFechaPedido();
        }


        return pedido
                .getFechaCreacion();
    }


    private LocalDateTime obtenerFechaCotizacion(
            Cotizacion cotizacion) {

        if (
                cotizacion.getFechaEmision()
                        != null
        ) {

            return cotizacion
                    .getFechaEmision();
        }


        return cotizacion
                .getFechaCreacion();
    }


    // =====================================================
    // PEDIDO VÁLIDO
    // =====================================================

    private boolean pedidoCuentaComoVenta(
            Pedido pedido) {

        if (
                pedido == null
        ) {

            return false;
        }


        String estado =
                pedido.getEstado();


        if (
                estado == null
        ) {

            return true;
        }


        return !estado
                .trim()
                .equalsIgnoreCase(
                        "CANCELADO"
                );
    }


    // =====================================================
    // NOMBRE DEL MES
    // =====================================================

    private String obtenerNombreMes(
            int numeroMes) {

        switch (
                numeroMes
        ) {

            case 1:
                return "Enero";

            case 2:
                return "Febrero";

            case 3:
                return "Marzo";

            case 4:
                return "Abril";

            case 5:
                return "Mayo";

            case 6:
                return "Junio";

            case 7:
                return "Julio";

            case 8:
                return "Agosto";

            case 9:
                return "Septiembre";

            case 10:
                return "Octubre";

            case 11:
                return "Noviembre";

            case 12:
                return "Diciembre";

            default:
                return "";
        }
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
                authentication == null ||
                !authentication
                        .isAuthenticated()
        ) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado"
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
                    "El usuario autenticado no es válido"
            );
        }


        return (UsuarioAutenticado)
                principal;
    }
}
package com.flowb2b.DashboardGerencialResumenDTO;

import java.util.ArrayList;
import java.util.List;

public class DashboardGerencialAnaliticaDTO {

    private DashboardGerencialResumenDTO resumen;

    private List<DashboardGerencialVentaMensualDTO>
            ventasMensuales;

    private List<DashboardGerencialPedidoMensualDTO>
            pedidosMensuales;

    private List<DashboardGerencialProductoDTO>
            topProductos;

    private List<DashboardGerencialCotizacionEstadoDTO>
            cotizacionesPorEstado;


    public DashboardGerencialAnaliticaDTO() {

        this.ventasMensuales =
                new ArrayList<>();

        this.pedidosMensuales =
                new ArrayList<>();

        this.topProductos =
                new ArrayList<>();

        this.cotizacionesPorEstado =
                new ArrayList<>();
    }


    public DashboardGerencialResumenDTO getResumen() {
        return resumen;
    }


    public void setResumen(
            DashboardGerencialResumenDTO resumen) {

        this.resumen = resumen;
    }


    public List<DashboardGerencialVentaMensualDTO>
            getVentasMensuales() {

        return ventasMensuales;
    }


    public void setVentasMensuales(
            List<DashboardGerencialVentaMensualDTO>
                    ventasMensuales) {

        this.ventasMensuales =
                ventasMensuales;
    }


    public List<DashboardGerencialPedidoMensualDTO>
            getPedidosMensuales() {

        return pedidosMensuales;
    }


    public void setPedidosMensuales(
            List<DashboardGerencialPedidoMensualDTO>
                    pedidosMensuales) {

        this.pedidosMensuales =
                pedidosMensuales;
    }


    public List<DashboardGerencialProductoDTO>
            getTopProductos() {

        return topProductos;
    }


    public void setTopProductos(
            List<DashboardGerencialProductoDTO>
                    topProductos) {

        this.topProductos =
                topProductos;
    }


    public List<DashboardGerencialCotizacionEstadoDTO>
            getCotizacionesPorEstado() {

        return cotizacionesPorEstado;
    }


    public void setCotizacionesPorEstado(
            List<DashboardGerencialCotizacionEstadoDTO>
                    cotizacionesPorEstado) {

        this.cotizacionesPorEstado =
                cotizacionesPorEstado;
    }
}
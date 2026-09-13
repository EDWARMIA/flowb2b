package com.flowb2b.DashboardGerencialResumenDTO;

import java.math.BigDecimal;

public class DashboardGerencialResumenDTO {

    private BigDecimal ventasTotales;
    private Long totalPedidos;
    private BigDecimal ticketPromedio;
    private BigDecimal tasaConversion;
    private Long totalCotizaciones;

    public DashboardGerencialResumenDTO() {
    }

    public DashboardGerencialResumenDTO(
            BigDecimal ventasTotales,
            Long totalPedidos,
            BigDecimal ticketPromedio,
            BigDecimal tasaConversion,
            Long totalCotizaciones) {

        this.ventasTotales = ventasTotales;
        this.totalPedidos = totalPedidos;
        this.ticketPromedio = ticketPromedio;
        this.tasaConversion = tasaConversion;
        this.totalCotizaciones = totalCotizaciones;
    }

    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public BigDecimal getTasaConversion() {
        return tasaConversion;
    }

    public void setTasaConversion(BigDecimal tasaConversion) {
        this.tasaConversion = tasaConversion;
    }

    public Long getTotalCotizaciones() {
        return totalCotizaciones;
    }

    public void setTotalCotizaciones(Long totalCotizaciones) {
        this.totalCotizaciones = totalCotizaciones;
    }
}
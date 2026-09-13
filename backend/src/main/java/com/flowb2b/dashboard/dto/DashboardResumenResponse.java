package com.flowb2b.dashboard.dto;

public class DashboardResumenResponse {

    private long solicitudes;
    private long cotizaciones;
    private long pedidos;
    private long aprobacionesPendientes;

    public DashboardResumenResponse() {
    }

    public DashboardResumenResponse(
            long solicitudes,
            long cotizaciones,
            long pedidos,
            long aprobacionesPendientes) {

        this.solicitudes = solicitudes;
        this.cotizaciones = cotizaciones;
        this.pedidos = pedidos;
        this.aprobacionesPendientes =
                aprobacionesPendientes;
    }

    public long getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(
            long solicitudes) {

        this.solicitudes = solicitudes;
    }

    public long getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(
            long cotizaciones) {

        this.cotizaciones = cotizaciones;
    }

    public long getPedidos() {
        return pedidos;
    }

    public void setPedidos(
            long pedidos) {

        this.pedidos = pedidos;
    }

    public long getAprobacionesPendientes() {
        return aprobacionesPendientes;
    }

    public void setAprobacionesPendientes(
            long aprobacionesPendientes) {

        this.aprobacionesPendientes =
                aprobacionesPendientes;
    }
}
package com.flowb2b.DashboardGerencialResumenDTO;

import java.math.BigDecimal;

public class DashboardGerencialProductoDTO {

    private Long productoId;

    private String producto;

    private BigDecimal cantidadVendida;

    private BigDecimal ventas;


    public DashboardGerencialProductoDTO() {
    }


    public DashboardGerencialProductoDTO(
            Long productoId,
            String producto,
            BigDecimal cantidadVendida,
            BigDecimal ventas) {

        this.productoId = productoId;
        this.producto = producto;
        this.cantidadVendida = cantidadVendida;
        this.ventas = ventas;
    }


    public Long getProductoId() {
        return productoId;
    }


    public void setProductoId(
            Long productoId) {

        this.productoId = productoId;
    }


    public String getProducto() {
        return producto;
    }


    public void setProducto(
            String producto) {

        this.producto = producto;
    }


    public BigDecimal getCantidadVendida() {
        return cantidadVendida;
    }


    public void setCantidadVendida(
            BigDecimal cantidadVendida) {

        this.cantidadVendida =
                cantidadVendida;
    }


    public BigDecimal getVentas() {
        return ventas;
    }


    public void setVentas(
            BigDecimal ventas) {

        this.ventas = ventas;
    }
}
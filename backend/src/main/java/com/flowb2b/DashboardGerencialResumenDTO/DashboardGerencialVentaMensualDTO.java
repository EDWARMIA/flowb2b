package com.flowb2b.DashboardGerencialResumenDTO;

import java.math.BigDecimal;

public class DashboardGerencialVentaMensualDTO {

    private String periodo;

    private String mes;

    private BigDecimal ventas;

    private Long pedidos;


    public DashboardGerencialVentaMensualDTO() {
    }


    public DashboardGerencialVentaMensualDTO(
            String periodo,
            String mes,
            BigDecimal ventas,
            Long pedidos) {

        this.periodo = periodo;
        this.mes = mes;
        this.ventas = ventas;
        this.pedidos = pedidos;
    }


    public String getPeriodo() {
        return periodo;
    }


    public void setPeriodo(
            String periodo) {

        this.periodo = periodo;
    }


    public String getMes() {
        return mes;
    }


    public void setMes(
            String mes) {

        this.mes = mes;
    }


    public BigDecimal getVentas() {
        return ventas;
    }


    public void setVentas(
            BigDecimal ventas) {

        this.ventas = ventas;
    }


    public Long getPedidos() {
        return pedidos;
    }


    public void setPedidos(
            Long pedidos) {

        this.pedidos = pedidos;
    }
}
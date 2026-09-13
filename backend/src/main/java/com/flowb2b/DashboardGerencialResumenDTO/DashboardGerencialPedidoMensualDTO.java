package com.flowb2b.DashboardGerencialResumenDTO;

public class DashboardGerencialPedidoMensualDTO {

    private String periodo;

    private String mes;

    private Long pedidos;


    public DashboardGerencialPedidoMensualDTO() {
    }


    public DashboardGerencialPedidoMensualDTO(
            String periodo,
            String mes,
            Long pedidos) {

        this.periodo = periodo;
        this.mes = mes;
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


    public Long getPedidos() {
        return pedidos;
    }


    public void setPedidos(
            Long pedidos) {

        this.pedidos = pedidos;
    }
}
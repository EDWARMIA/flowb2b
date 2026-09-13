package com.flowb2b.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventarioResponseDTO {

    private Long idInventario;
    private Long empresaId;
    private Long productoId;

    private BigDecimal stockActual;
    private BigDecimal stockReservado;
    private BigDecimal stockMinimo;

    private BigDecimal stockDisponible;

    private LocalDateTime fechaActualizacion;

    public Long getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Long idInventario) {
        this.idInventario = idInventario;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public BigDecimal getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(BigDecimal stockReservado) {
        this.stockReservado = stockReservado;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(
            BigDecimal stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
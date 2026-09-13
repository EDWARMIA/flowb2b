package com.flowb2b.inventario.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class InventarioRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "El stock actual es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El stock actual no puede ser negativo"
    )
    private BigDecimal stockActual;

    @NotNull(message = "El stock reservado es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El stock reservado no puede ser negativo"
    )
    private BigDecimal stockReservado;

    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El stock mínimo no puede ser negativo"
    )
    private BigDecimal stockMinimo;

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
}
package com.flowb2b.cotizacion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DetalleCotizacionRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @Size(
        max = 255,
        message = "La descripción no puede superar 255 caracteres"
    )
    private String descripcion;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(
        value = "0.01",
        inclusive = true,
        message = "La cantidad debe ser mayor que cero"
    )
    private BigDecimal cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El precio unitario no puede ser negativo"
    )
    private BigDecimal precioUnitario;

    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El descuento no puede ser negativo"
    )
    @DecimalMax(
        value = "100.00",
        inclusive = true,
        message = "El descuento no puede superar 100%"
    )
    private BigDecimal porcentajeDescuento;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(
            BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
}
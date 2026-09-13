package com.flowb2b.cotizacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CotizacionRequestDTO {

    @NotNull(message = "La solicitud es obligatoria")
    private Long solicitudId;

    @NotBlank(message = "El código es obligatorio")
    @Size(
        max = 30,
        message = "El código no puede superar 30 caracteres"
    )
    private String codigo;

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

    private LocalDateTime fechaVencimiento;

    private String observaciones;

    @NotEmpty(message = "La cotización debe tener al menos un producto")
    @Valid
    private List<DetalleCotizacionRequestDTO> detalles;

    public Long getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(Long solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(
            BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(
            LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DetalleCotizacionRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(
            List<DetalleCotizacionRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
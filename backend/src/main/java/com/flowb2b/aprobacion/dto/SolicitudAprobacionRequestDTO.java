package com.flowb2b.aprobacion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitudAprobacionRequestDTO {

    @NotNull(message = "La cotización es obligatoria")
    private Long cotizacionId;

    @Size(
        max = 255,
        message = "El motivo no puede superar 255 caracteres"
    )
    private String motivo;

    // GETTERS Y SETTERS

    public Long getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(Long cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
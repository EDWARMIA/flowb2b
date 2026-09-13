package com.flowb2b.solicitud.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SolicitudRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El código es obligatorio")
    @Size(
        max = 30,
        message = "El código no puede superar 30 caracteres"
    )
    private String codigo;

    @NotBlank(message = "El origen es obligatorio")
    @Size(
        max = 30,
        message = "El origen no puede superar 30 caracteres"
    )
    private String origen;

    private String descripcion;

    @NotEmpty(message = "La solicitud debe tener al menos un producto")
    @Valid
    private List<DetalleSolicitudRequestDTO> detalles;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<DetalleSolicitudRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(
            List<DetalleSolicitudRequestDTO> detalles) {
        this.detalles = detalles;
    }
}
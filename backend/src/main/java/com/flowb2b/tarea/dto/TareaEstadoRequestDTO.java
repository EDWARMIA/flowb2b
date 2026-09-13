package com.flowb2b.tarea.dto;

import jakarta.validation.constraints.NotBlank;

public class TareaEstadoRequestDTO {

    @NotBlank
    private String estado;

    public TareaEstadoRequestDTO() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
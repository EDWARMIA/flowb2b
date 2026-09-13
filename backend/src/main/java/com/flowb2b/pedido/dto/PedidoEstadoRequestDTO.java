package com.flowb2b.pedido.dto;

import jakarta.validation.constraints.NotBlank;

public class PedidoEstadoRequestDTO {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private String comentario;

    public PedidoEstadoRequestDTO() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
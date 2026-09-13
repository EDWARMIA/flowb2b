package com.flowb2b.aprobacion.dto;

import jakarta.validation.constraints.Size;

public class ResponderAprobacionRequestDTO {

    @Size(
        max = 2000,
        message = "El comentario no puede superar 2000 caracteres"
    )
    private String comentario;

    // GETTERS Y SETTERS

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
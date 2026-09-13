package com.flowb2b.notificacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificacionRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotBlank
    @Size(max = 150)
    private String titulo;

    @NotBlank
    @Size(max = 500)
    private String mensaje;

    @Size(max = 50)
    private String tipo;

    @Size(max = 50)
    private String entidadTipo;

    private Long entidadId;

    public NotificacionRequestDTO() {
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(
            Long usuarioId) {

        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(
            String titulo) {

        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(
            String mensaje) {

        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(
            String tipo) {

        this.tipo = tipo;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public void setEntidadTipo(
            String entidadTipo) {

        this.entidadTipo = entidadTipo;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(
            Long entidadId) {

        this.entidadId = entidadId;
    }
}
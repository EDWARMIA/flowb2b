package com.flowb2b.notificacion.dto;

import java.time.LocalDateTime;

public class NotificacionResponseDTO {

    private Long idNotificacion;

    private Long empresaId;

    private Long usuarioId;

    private String titulo;

    private String mensaje;

    private String tipo;

    private String entidadTipo;

    private Long entidadId;

    private Boolean leida;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaLectura;

    public NotificacionResponseDTO() {
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(
            Long idNotificacion) {

        this.idNotificacion = idNotificacion;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(
            Long empresaId) {

        this.empresaId = empresaId;
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

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(
            Boolean leida) {

        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion) {

        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(
            LocalDateTime fechaLectura) {

        this.fechaLectura = fechaLectura;
    }
}
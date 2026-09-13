package com.flowb2b.aprobacion.dto;

import java.time.LocalDateTime;

public class AprobacionResponseDTO {

    private Long idAprobacion;
    private Long empresaId;
    private Long cotizacionId;
    private Long solicitanteId;
    private Long aprobadorId;
    private String estado;
    private String motivo;
    private String comentario;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    public Long getIdAprobacion() {
        return idAprobacion;
    }

    public void setIdAprobacion(Long idAprobacion) {
        this.idAprobacion = idAprobacion;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getCotizacionId() {
        return cotizacionId;
    }

    public void setCotizacionId(Long cotizacionId) {
        this.cotizacionId = cotizacionId;
    }

    public Long getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public Long getAprobadorId() {
        return aprobadorId;
    }

    public void setAprobadorId(Long aprobadorId) {
        this.aprobadorId = aprobadorId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
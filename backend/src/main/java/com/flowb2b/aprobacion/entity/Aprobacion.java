package com.flowb2b.aprobacion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aprobacion")
public class Aprobacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aprobacion")
    private Long idAprobacion;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cotizacion_id", nullable = false)
    private Long cotizacionId;

    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "aprobador_id")
    private Long aprobadorId;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(
        name = "fecha_solicitud",
        insertable = false,
        updatable = false
    )
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;


    // =========================
    // GETTERS Y SETTERS
    // =========================

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
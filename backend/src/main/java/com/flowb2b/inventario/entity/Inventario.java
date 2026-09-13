package com.flowb2b.inventario.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(
        name = "stock_actual",
        nullable = false,
        precision = 14,
        scale = 2
    )
    private BigDecimal stockActual;

    @Column(
        name = "stock_reservado",
        nullable = false,
        precision = 14,
        scale = 2
    )
    private BigDecimal stockReservado;

    @Column(
        name = "stock_minimo",
        nullable = false,
        precision = 14,
        scale = 2
    )
    private BigDecimal stockMinimo;

    @Column(
        name = "fecha_actualizacion",
        insertable = false,
        updatable = false
    )
    private LocalDateTime fechaActualizacion;

    public Long getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Long idInventario) {
        this.idInventario = idInventario;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getStockActual() {
        return stockActual;
    }

    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }

    public BigDecimal getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(BigDecimal stockReservado) {
        this.stockReservado = stockReservado;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
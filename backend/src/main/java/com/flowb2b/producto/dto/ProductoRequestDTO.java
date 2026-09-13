package com.flowb2b.producto.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductoRequestDTO {

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(
        max = 50,
        message = "El código no puede superar los 50 caracteres"
    )
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(
        max = 150,
        message = "El nombre no puede superar los 150 caracteres"
    )
    private String nombre;

    private String descripcion;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(
        max = 30,
        message = "La unidad de medida no puede superar los 30 caracteres"
    )
    private String unidadMedida;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(
        value = "0.00",
        inclusive = true,
        message = "El precio base no puede ser negativo"
    )
    private BigDecimal precioBase;

    private Boolean estado;

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
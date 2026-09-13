package com.flowb2b.producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaProductoRequestDTO {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(
        max = 100,
        message = "El nombre no puede superar los 100 caracteres"
    )
    private String nombre;

    @Size(
        max = 255,
        message = "La descripción no puede superar los 255 caracteres"
    )
    private String descripcion;

    private Boolean estado;

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

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
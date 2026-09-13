package com.flowb2b.administracion.dto;

import java.util.HashSet;
import java.util.Set;

public class RolAdministracionRequestDTO {

    private String nombre;
    private String descripcion;
    private Boolean estado = true;
    private Set<Long> permisosIds = new HashSet<>();

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

    public Set<Long> getPermisosIds() {
        return permisosIds;
    }

    public void setPermisosIds(Set<Long> permisosIds) {
        this.permisosIds = permisosIds;
    }
}
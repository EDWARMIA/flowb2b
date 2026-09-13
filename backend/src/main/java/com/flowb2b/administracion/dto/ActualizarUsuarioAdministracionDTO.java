package com.flowb2b.administracion.dto;

import java.util.HashSet;
import java.util.Set;

public class ActualizarUsuarioAdministracionDTO {

    private Boolean estado;
    private Set<Long> rolesIds = new HashSet<>();

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Set<Long> getRolesIds() {
        return rolesIds;
    }

    public void setRolesIds(Set<Long> rolesIds) {
        this.rolesIds = rolesIds;
    }
}
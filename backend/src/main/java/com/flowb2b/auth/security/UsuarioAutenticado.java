package com.flowb2b.auth.security;

public class UsuarioAutenticado {

    private final Long idUsuario;
    private final Long empresaId;
    private final String correo;

    public UsuarioAutenticado(
            Long idUsuario,
            Long empresaId,
            String correo) {

        this.idUsuario = idUsuario;
        this.empresaId = empresaId;
        this.correo = correo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getCorreo() {
        return correo;
    }
}
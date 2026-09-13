package com.flowb2b.auditoria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auditoria.dto.AuditoriaResponseDTO;
import com.flowb2b.auditoria.entity.Auditoria;
import com.flowb2b.auditoria.repository.AuditoriaRepository;
import com.flowb2b.auth.security.UsuarioAutenticado;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public void registrar(
            String accion,
            String entidad,
            Long entidadId,
            String datosAnteriores,
            String datosNuevos,
            String ip) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        if (accion == null || accion.isBlank()) {
            throw new IllegalArgumentException(
                    "La acción de auditoría es obligatoria");
        }

        if (entidad == null || entidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La entidad de auditoría es obligatoria");
        }

        Auditoria auditoria = new Auditoria();

        auditoria.setEmpresaId(
                usuario.getEmpresaId());

        auditoria.setUsuarioId(
                usuario.getIdUsuario());

        auditoria.setAccion(
                accion.trim().toUpperCase());

        auditoria.setEntidad(
                entidad.trim().toUpperCase());

        auditoria.setEntidadId(
                entidadId);

        auditoria.setDatosAnteriores(
                normalizarJson(datosAnteriores));

        auditoria.setDatosNuevos(
                normalizarJson(datosNuevos));

        if (ip != null && !ip.isBlank()) {

            String ipSegura = ip.trim();

            if (ipSegura.length() > 45) {
                ipSegura =
                        ipSegura.substring(0, 45);
            }

            auditoria.setIp(ipSegura);
        }

        auditoriaRepository.save(auditoria);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponseDTO> listar() {

        Long empresaId =
                obtenerUsuarioAutenticado()
                        .getEmpresaId();

        List<Auditoria> auditorias =
                auditoriaRepository
                        .findByEmpresaIdOrderByFechaDesc(
                                empresaId);

        return convertirLista(auditorias);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponseDTO> listarPorEntidad(
            String entidad,
            Long entidadId) {

        Long empresaId =
                obtenerUsuarioAutenticado()
                        .getEmpresaId();

        if (entidad == null || entidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La entidad es obligatoria");
        }

        if (entidadId == null) {
            throw new IllegalArgumentException(
                    "El ID de la entidad es obligatorio");
        }

        List<Auditoria> auditorias =
                auditoriaRepository
                        .findByEmpresaIdAndEntidadAndEntidadIdOrderByFechaDesc(
                                empresaId,
                                entidad.trim().toUpperCase(),
                                entidadId);

        return convertirLista(auditorias);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponseDTO> listarPorUsuario(
            Long usuarioId) {

        Long empresaId =
                obtenerUsuarioAutenticado()
                        .getEmpresaId();

        if (usuarioId == null) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio");
        }

        List<Auditoria> auditorias =
                auditoriaRepository
                        .findByEmpresaIdAndUsuarioIdOrderByFechaDesc(
                                empresaId,
                                usuarioId);

        return convertirLista(auditorias);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponseDTO> listarPorAccion(
            String accion) {

        Long empresaId =
                obtenerUsuarioAutenticado()
                        .getEmpresaId();

        if (accion == null || accion.isBlank()) {
            throw new IllegalArgumentException(
                    "La acción es obligatoria");
        }

        List<Auditoria> auditorias =
                auditoriaRepository
                        .findByEmpresaIdAndAccionOrderByFechaDesc(
                                empresaId,
                                accion.trim().toUpperCase());

        return convertirLista(auditorias);
    }

    private List<AuditoriaResponseDTO> convertirLista(
            List<Auditoria> auditorias) {

        List<AuditoriaResponseDTO> respuesta =
                new ArrayList<>();

        for (Auditoria auditoria : auditorias) {
            respuesta.add(
                    convertirAResponse(auditoria));
        }

        return respuesta;
    }

    private AuditoriaResponseDTO convertirAResponse(
            Auditoria auditoria) {

        AuditoriaResponseDTO response =
                new AuditoriaResponseDTO();

        response.setIdAuditoria(
                auditoria.getIdAuditoria());

        response.setEmpresaId(
                auditoria.getEmpresaId());

        response.setUsuarioId(
                auditoria.getUsuarioId());

        response.setAccion(
                auditoria.getAccion());

        response.setEntidad(
                auditoria.getEntidad());

        response.setEntidadId(
                auditoria.getEntidadId());

        response.setDatosAnteriores(
                auditoria.getDatosAnteriores());

        response.setDatosNuevos(
                auditoria.getDatosNuevos());

        response.setIp(
                auditoria.getIp());

        response.setFecha(
                auditoria.getFecha());

        return response;
    }

    private String normalizarJson(String json) {

        if (json == null || json.isBlank()) {
            return null;
        }

        return json.trim();
    }

    private UsuarioAutenticado obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Usuario no autenticado");
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "No se pudo obtener el usuario autenticado");
        }

        return (UsuarioAutenticado) principal;
    }
}
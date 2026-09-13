package com.flowb2b.historial.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.historial.dto.HistorialEstadoResponseDTO;
import com.flowb2b.historial.entity.HistorialEstado;
import com.flowb2b.historial.repository.HistorialEstadoRepository;

@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoService(
            HistorialEstadoRepository historialEstadoRepository) {

        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Transactional
    public HistorialEstado registrar(
            String tipoEntidad,
            Long entidadId,
            String estadoAnterior,
            String estadoNuevo,
            String comentario) {

        UsuarioAutenticado usuario = obtenerUsuarioAutenticado();

        if (tipoEntidad == null || tipoEntidad.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de entidad es obligatorio");
        }

        if (entidadId == null) {
            throw new IllegalArgumentException(
                    "El ID de la entidad es obligatorio");
        }

        if (estadoNuevo == null || estadoNuevo.isBlank()) {
            throw new IllegalArgumentException(
                    "El estado nuevo es obligatorio");
        }

        HistorialEstado historial = new HistorialEstado();

        historial.setEmpresaId(usuario.getEmpresaId());
        historial.setTipoEntidad(
                tipoEntidad.trim().toUpperCase());
        historial.setEntidadId(entidadId);

        if (estadoAnterior != null
                && !estadoAnterior.isBlank()) {

            historial.setEstadoAnterior(
                    estadoAnterior.trim().toUpperCase());

        } else {
            historial.setEstadoAnterior(null);
        }

        historial.setEstadoNuevo(
                estadoNuevo.trim().toUpperCase());

        historial.setUsuarioId(
                usuario.getIdUsuario());

        if (comentario != null
                && !comentario.isBlank()) {

            String comentarioLimpio = comentario.trim();

            if (comentarioLimpio.length() > 500) {
                comentarioLimpio =
                        comentarioLimpio.substring(0, 500);
            }

            historial.setComentario(comentarioLimpio);

        } else {
            historial.setComentario(null);
        }

        return historialEstadoRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoResponseDTO> listar() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        List<HistorialEstado> historiales =
                historialEstadoRepository
                        .findByEmpresaIdOrderByFechaDesc(
                                usuario.getEmpresaId());

        return convertirLista(
                historialesSeguros(historiales));
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoResponseDTO> listarPorEntidad(
            String tipoEntidad,
            Long entidadId) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        if (tipoEntidad == null || tipoEntidad.isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de entidad es obligatorio");
        }

        if (entidadId == null) {
            throw new IllegalArgumentException(
                    "El ID de la entidad es obligatorio");
        }

        List<HistorialEstado> historiales =
                historialEstadoRepository
                        .findByEmpresaIdAndTipoEntidadAndEntidadIdOrderByFechaDesc(
                                usuario.getEmpresaId(),
                                tipoEntidad.trim().toUpperCase(),
                                entidadId);

        return convertirLista(
                historialesSeguros(historiales));
    }

    private List<HistorialEstado> historialesSeguros(
            List<HistorialEstado> historiales) {

        if (historiales == null) {
            return new ArrayList<>();
        }

        return historiales;
    }

    private List<HistorialEstadoResponseDTO> convertirLista(
            List<HistorialEstado> historiales) {

        List<HistorialEstadoResponseDTO> respuestas =
                new ArrayList<>();

        for (HistorialEstado historial : historiales) {
            respuestas.add(
                    convertirAResponse(historial));
        }

        return respuestas;
    }

    private HistorialEstadoResponseDTO convertirAResponse(
            HistorialEstado historial) {

        HistorialEstadoResponseDTO response =
                new HistorialEstadoResponseDTO();

        response.setIdHistorial(
                historial.getIdHistorial());

        response.setEmpresaId(
                historial.getEmpresaId());

        response.setTipoEntidad(
                historial.getTipoEntidad());

        response.setEntidadId(
                historial.getEntidadId());

        response.setEstadoAnterior(
                historial.getEstadoAnterior());

        response.setEstadoNuevo(
                historial.getEstadoNuevo());

        response.setUsuarioId(
                historial.getUsuarioId());

        response.setComentario(
                historial.getComentario());

        response.setFecha(
                historial.getFecha());

        return response;
    }

    private UsuarioAutenticado obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "No se pudo obtener el usuario autenticado");
        }

        return (UsuarioAutenticado)
                authentication.getPrincipal();
    }
}
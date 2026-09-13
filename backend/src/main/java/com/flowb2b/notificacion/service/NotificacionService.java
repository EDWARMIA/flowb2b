package com.flowb2b.notificacion.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.notificacion.dto.NotificacionRequestDTO;
import com.flowb2b.notificacion.dto.NotificacionResponseDTO;
import com.flowb2b.notificacion.entity.Notificacion;
import com.flowb2b.notificacion.repository.NotificacionRepository;
import com.flowb2b.usuario.entity.Usuario;
import com.flowb2b.usuario.repository.UsuarioRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionService(
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository) {

        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO>
            listarMisNotificaciones() {

        UsuarioAutenticado autenticado =
                obtenerUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuarioIdAndEmpresaIdOrderByFechaCreacionDesc(
                                autenticado.getIdUsuario(),
                                autenticado.getEmpresaId());

        List<NotificacionResponseDTO> respuestas =
                new ArrayList<>();

        for (Notificacion notificacion :
                notificaciones) {

            respuestas.add(
                    convertirAResponse(
                            notificacion));
        }

        return respuestas;
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO>
            listarMisNoLeidas() {

        UsuarioAutenticado autenticado =
                obtenerUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuarioIdAndEmpresaIdAndLeidaOrderByFechaCreacionDesc(
                                autenticado.getIdUsuario(),
                                autenticado.getEmpresaId(),
                                false);

        List<NotificacionResponseDTO> respuestas =
                new ArrayList<>();

        for (Notificacion notificacion :
                notificaciones) {

            respuestas.add(
                    convertirAResponse(
                            notificacion));
        }

        return respuestas;
    }

    @Transactional(readOnly = true)
    public NotificacionResponseDTO obtenerPorId(
            Long idNotificacion) {

        UsuarioAutenticado autenticado =
                obtenerUsuarioAutenticado();

        Notificacion notificacion =
                notificacionRepository
                        .findByIdNotificacionAndUsuarioIdAndEmpresaId(
                                idNotificacion,
                                autenticado.getIdUsuario(),
                                autenticado.getEmpresaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notificación no encontrada"));

        return convertirAResponse(
                notificacion);
    }

    @Transactional
    public NotificacionResponseDTO crear(
            NotificacionRequestDTO dto) {

        UsuarioAutenticado autenticado =
                obtenerUsuarioAutenticado();

        Long empresaId =
                autenticado.getEmpresaId();

        Usuario destinatario =
                usuarioRepository
                        .findById(
                                dto.getUsuarioId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Usuario destinatario no encontrado"));

        if (!empresaId.equals(
                destinatario.getEmpresaId())) {

            throw new IllegalArgumentException(
                    "El usuario destinatario no pertenece a la empresa");
        }

        Notificacion notificacion =
                new Notificacion();

        notificacion.setEmpresaId(
                empresaId);

        notificacion.setUsuarioId(
                destinatario.getIdUsuario());

        notificacion.setTitulo(
                dto.getTitulo()
                        .trim());

        notificacion.setMensaje(
                dto.getMensaje()
                        .trim());

        if (
            dto.getTipo() != null &&
            !dto.getTipo().isBlank()
        ) {

            notificacion.setTipo(
                    dto.getTipo()
                            .trim()
                            .toUpperCase());

        } else {

            notificacion.setTipo(
                    null);
        }

        if (
            dto.getEntidadTipo() != null &&
            !dto.getEntidadTipo().isBlank()
        ) {

            notificacion.setEntidadTipo(
                    dto.getEntidadTipo()
                            .trim()
                            .toUpperCase());

        } else {

            notificacion.setEntidadTipo(
                    null);
        }

        notificacion.setEntidadId(
                dto.getEntidadId());

        notificacion.setLeida(
                false);

        notificacion.setFechaLectura(
                null);

        Notificacion guardada =
                notificacionRepository
                        .save(
                                notificacion);

        return convertirAResponse(
                guardada);
    }

    @Transactional
    public NotificacionResponseDTO marcarComoLeida(
            Long idNotificacion) {

        UsuarioAutenticado autenticado =
                obtenerUsuarioAutenticado();

        Notificacion notificacion =
                notificacionRepository
                        .findByIdNotificacionAndUsuarioIdAndEmpresaId(
                                idNotificacion,
                                autenticado.getIdUsuario(),
                                autenticado.getEmpresaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notificación no encontrada"));

        if (
            !Boolean.TRUE.equals(
                    notificacion.getLeida())
        ) {

            notificacion.setLeida(
                    true);

            notificacion.setFechaLectura(
                    LocalDateTime.now());
        }

        Notificacion actualizada =
                notificacionRepository
                        .save(
                                notificacion);

        return convertirAResponse(
                actualizada);
    }

    private NotificacionResponseDTO convertirAResponse(
            Notificacion notificacion) {

        NotificacionResponseDTO response =
                new NotificacionResponseDTO();

        response.setIdNotificacion(
                notificacion.getIdNotificacion());

        response.setEmpresaId(
                notificacion.getEmpresaId());

        response.setUsuarioId(
                notificacion.getUsuarioId());

        response.setTitulo(
                notificacion.getTitulo());

        response.setMensaje(
                notificacion.getMensaje());

        response.setTipo(
                notificacion.getTipo());

        response.setEntidadTipo(
                notificacion.getEntidadTipo());

        response.setEntidadId(
                notificacion.getEntidadId());

        response.setLeida(
                notificacion.getLeida());

        response.setFechaCreacion(
                notificacion.getFechaCreacion());

        response.setFechaLectura(
                notificacion.getFechaLectura());

        return response;
    }

    private UsuarioAutenticado
            obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (
            authentication == null ||
            !(authentication.getPrincipal()
            instanceof UsuarioAutenticado)
        ) {

            throw new IllegalStateException(
                    "No se pudo obtener el usuario autenticado");
        }

        return (UsuarioAutenticado)
                authentication.getPrincipal();
    }
}
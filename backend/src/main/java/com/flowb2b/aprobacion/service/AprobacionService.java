package com.flowb2b.aprobacion.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.aprobacion.dto.AprobacionResponseDTO;
import com.flowb2b.aprobacion.dto.ResponderAprobacionRequestDTO;
import com.flowb2b.aprobacion.dto.SolicitudAprobacionRequestDTO;
import com.flowb2b.aprobacion.entity.Aprobacion;
import com.flowb2b.aprobacion.repository.AprobacionRepository;
import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.cotizacion.entity.Cotizacion;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.historial.service.HistorialEstadoService;

@Service
public class AprobacionService {

    private final AprobacionRepository aprobacionRepository;
    private final CotizacionRepository cotizacionRepository;
    private final HistorialEstadoService historialEstadoService;
    private final AuditoriaService auditoriaService;

    public AprobacionService(
            AprobacionRepository aprobacionRepository,
            CotizacionRepository cotizacionRepository,
            HistorialEstadoService historialEstadoService,
            AuditoriaService auditoriaService) {

        this.aprobacionRepository = aprobacionRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.historialEstadoService = historialEstadoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<AprobacionResponseDTO> listar() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        List<Aprobacion> aprobaciones =
                aprobacionRepository.findByEmpresaId(
                        usuario.getEmpresaId());

        List<AprobacionResponseDTO> respuesta =
                new ArrayList<>();

        for (Aprobacion aprobacion : aprobaciones) {
            respuesta.add(
                    convertirAResponse(aprobacion));
        }

        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<AprobacionResponseDTO> listarPendientes() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        List<Aprobacion> aprobaciones =
                aprobacionRepository
                        .findByEmpresaIdAndEstado(
                                usuario.getEmpresaId(),
                                "PENDIENTE");

        List<AprobacionResponseDTO> respuesta =
                new ArrayList<>();

        for (Aprobacion aprobacion : aprobaciones) {
            respuesta.add(
                    convertirAResponse(aprobacion));
        }

        return respuesta;
    }

    @Transactional(readOnly = true)
    public AprobacionResponseDTO obtenerPorId(
            Long idAprobacion) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Aprobacion aprobacion =
                aprobacionRepository
                        .findByIdAprobacionAndEmpresaId(
                                idAprobacion,
                                usuario.getEmpresaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Aprobación no encontrada"));

        return convertirAResponse(aprobacion);
    }

    @Transactional
    public AprobacionResponseDTO solicitar(
            SolicitudAprobacionRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Long solicitanteId =
                usuario.getIdUsuario();

        Cotizacion cotizacion =
                cotizacionRepository
                        .findByIdCotizacionAndEmpresaId(
                                dto.getCotizacionId(),
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cotización no encontrada"));

        if (!"BORRADOR".equalsIgnoreCase(
                cotizacion.getEstado())) {

            throw new IllegalArgumentException(
                    "Solo una cotización en estado BORRADOR "
                            + "puede enviarse a aprobación");
        }

        boolean existePendiente =
                aprobacionRepository
                        .existsByCotizacionIdAndEmpresaIdAndEstado(
                                cotizacion.getIdCotizacion(),
                                empresaId,
                                "PENDIENTE");

        if (existePendiente) {

            throw new IllegalArgumentException(
                    "La cotización ya tiene una aprobación pendiente");
        }

        Aprobacion aprobacion =
                new Aprobacion();

        aprobacion.setEmpresaId(
                empresaId);

        aprobacion.setCotizacionId(
                cotizacion.getIdCotizacion());

        aprobacion.setSolicitanteId(
                solicitanteId);

        aprobacion.setAprobadorId(
                null);

        aprobacion.setEstado(
                "PENDIENTE");

        aprobacion.setMotivo(
                dto.getMotivo());

        aprobacion.setComentario(
                null);

        aprobacion.setFechaRespuesta(
                null);

        Aprobacion aprobacionGuardada =
                aprobacionRepository.save(
                        aprobacion);

        String estadoAnterior =
                cotizacion.getEstado();

        cotizacion.setEstado(
                "PENDIENTE_APROBACION");

        cotizacionRepository.save(
                cotizacion);

        historialEstadoService.registrar(
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                estadoAnterior,
                "PENDIENTE_APROBACION",
                "Cotización enviada a aprobación");

        auditoriaService.registrar(
                "SOLICITAR_APROBACION",
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                "{\"estado\":\""
                        + estadoAnterior
                        + "\"}",
                "{\"estado\":\"PENDIENTE_APROBACION\"}",
                null);

        return convertirAResponse(
                aprobacionGuardada);
    }

    @Transactional
    public AprobacionResponseDTO aprobar(
            Long idAprobacion,
            ResponderAprobacionRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Long aprobadorId =
                usuario.getIdUsuario();

        Aprobacion aprobacion =
                obtenerAprobacionPendiente(
                        idAprobacion,
                        empresaId);

        Cotizacion cotizacion =
                obtenerCotizacion(
                        aprobacion.getCotizacionId(),
                        empresaId);

        if (aprobacion.getSolicitanteId() != null
                && aprobacion.getSolicitanteId().equals(aprobadorId)) {

            throw new IllegalArgumentException(
                    "El usuario que solicitó la aprobación "
                            + "no puede aprobar su propia solicitud");
        }

        if (!"PENDIENTE_APROBACION"
                .equalsIgnoreCase(
                        cotizacion.getEstado())) {

            throw new IllegalArgumentException(
                    "La cotización no se encuentra "
                            + "pendiente de aprobación");
        }

        aprobacion.setEstado(
                "APROBADA");

        aprobacion.setAprobadorId(
                aprobadorId);

        aprobacion.setComentario(
                dto.getComentario());

        aprobacion.setFechaRespuesta(
                LocalDateTime.now());

        Aprobacion aprobacionGuardada =
                aprobacionRepository.save(
                        aprobacion);

        String estadoAnterior =
                cotizacion.getEstado();

        cotizacion.setEstado(
                "APROBADA");

        cotizacionRepository.save(
                cotizacion);

        String comentarioHistorial =
                dto.getComentario();

        if (comentarioHistorial == null
                || comentarioHistorial.isBlank()) {

            comentarioHistorial =
                    "Cotización aprobada";
        }

        historialEstadoService.registrar(
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                estadoAnterior,
                "APROBADA",
                comentarioHistorial);

        auditoriaService.registrar(
                "APROBAR",
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                "{\"estado\":\""
                        + estadoAnterior
                        + "\"}",
                "{\"estado\":\"APROBADA\"}",
                null);

        return convertirAResponse(
                aprobacionGuardada);
    }

    @Transactional
    public AprobacionResponseDTO rechazar(
            Long idAprobacion,
            ResponderAprobacionRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Long aprobadorId =
                usuario.getIdUsuario();

        Aprobacion aprobacion =
                obtenerAprobacionPendiente(
                        idAprobacion,
                        empresaId);

        Cotizacion cotizacion =
                obtenerCotizacion(
                        aprobacion.getCotizacionId(),
                        empresaId);

        if (!"PENDIENTE_APROBACION"
                .equalsIgnoreCase(
                        cotizacion.getEstado())) {

            throw new IllegalArgumentException(
                    "La cotización no se encuentra "
                            + "pendiente de aprobación");
        }

        aprobacion.setEstado(
                "RECHAZADA");

        aprobacion.setAprobadorId(
                aprobadorId);

        aprobacion.setComentario(
                dto.getComentario());

        aprobacion.setFechaRespuesta(
                LocalDateTime.now());

        Aprobacion aprobacionGuardada =
                aprobacionRepository.save(
                        aprobacion);

        String estadoAnterior =
                cotizacion.getEstado();

        cotizacion.setEstado(
                "RECHAZADA");

        cotizacionRepository.save(
                cotizacion);

        String comentarioHistorial =
                dto.getComentario();

        if (comentarioHistorial == null
                || comentarioHistorial.isBlank()) {

            comentarioHistorial =
                    "Cotización rechazada";
        }

        historialEstadoService.registrar(
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                estadoAnterior,
                "RECHAZADA",
                comentarioHistorial);

        auditoriaService.registrar(
                "RECHAZAR",
                "COTIZACION",
                cotizacion.getIdCotizacion(),
                "{\"estado\":\""
                        + estadoAnterior
                        + "\"}",
                "{\"estado\":\"RECHAZADA\"}",
                null);

        return convertirAResponse(
                aprobacionGuardada);
    }

    private Aprobacion obtenerAprobacionPendiente(
            Long idAprobacion,
            Long empresaId) {

        Aprobacion aprobacion =
                aprobacionRepository
                        .findByIdAprobacionAndEmpresaId(
                                idAprobacion,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Aprobación no encontrada"));

        if (!"PENDIENTE".equalsIgnoreCase(
                aprobacion.getEstado())) {

            throw new IllegalArgumentException(
                    "La aprobación ya fue respondida");
        }

        return aprobacion;
    }

    private Cotizacion obtenerCotizacion(
            Long idCotizacion,
            Long empresaId) {

        return cotizacionRepository
                .findByIdCotizacionAndEmpresaId(
                        idCotizacion,
                        empresaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cotización no encontrada"));
    }

    private AprobacionResponseDTO convertirAResponse(
            Aprobacion aprobacion) {

        AprobacionResponseDTO response =
                new AprobacionResponseDTO();

        response.setIdAprobacion(
                aprobacion.getIdAprobacion());

        response.setEmpresaId(
                aprobacion.getEmpresaId());

        response.setCotizacionId(
                aprobacion.getCotizacionId());

        response.setSolicitanteId(
                aprobacion.getSolicitanteId());

        response.setAprobadorId(
                aprobacion.getAprobadorId());

        response.setEstado(
                aprobacion.getEstado());

        response.setMotivo(
                aprobacion.getMotivo());

        response.setComentario(
                aprobacion.getComentario());

        response.setFechaSolicitud(
                aprobacion.getFechaSolicitud());

        response.setFechaRespuesta(
                aprobacion.getFechaRespuesta());

        return response;
    }

    private UsuarioAutenticado
            obtenerUsuarioAutenticado() {

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

        if (!(principal
                instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "No se pudo obtener el usuario autenticado");
        }

        return (UsuarioAutenticado) principal;
    }
}
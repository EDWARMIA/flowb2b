package com.flowb2b.tarea.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.historial.service.HistorialEstadoService;
import com.flowb2b.pedido.entity.Pedido;
import com.flowb2b.pedido.repository.PedidoRepository;
import com.flowb2b.tarea.dto.TareaEstadoRequestDTO;
import com.flowb2b.tarea.dto.TareaRequestDTO;
import com.flowb2b.tarea.dto.TareaResponseDTO;
import com.flowb2b.tarea.entity.Tarea;
import com.flowb2b.tarea.repository.TareaRepository;
import com.flowb2b.usuario.entity.Usuario;
import com.flowb2b.usuario.repository.UsuarioRepository;

@Service
public class TareaService {

    private static final Set<String> PRIORIDADES_VALIDAS =
            Set.of("BAJA", "MEDIA", "ALTA", "URGENTE");

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of(
                    "PENDIENTE",
                    "EN_PROCESO",
                    "COMPLETADA",
                    "CANCELADA"
            );

    private final TareaRepository tareaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialEstadoService historialEstadoService;

    public TareaService(
            TareaRepository tareaRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            HistorialEstadoService historialEstadoService) {

        this.tareaRepository = tareaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialEstadoService = historialEstadoService;
    }

    @Transactional(readOnly = true)
    public List<TareaResponseDTO> listar() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        List<Tarea> tareas =
                tareaRepository.findByEmpresaId(
                        usuario.getEmpresaId());

        List<TareaResponseDTO> respuestas =
                new ArrayList<>();

        for (Tarea tarea : tareas) {
            respuestas.add(
                    convertirAResponse(tarea));
        }

        return respuestas;
    }

    @Transactional(readOnly = true)
    public TareaResponseDTO obtenerPorId(
            Long idTarea) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Tarea tarea =
                tareaRepository
                        .findByIdTareaAndEmpresaId(
                                idTarea,
                                usuario.getEmpresaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tarea no encontrada"));

        return convertirAResponse(tarea);
    }

    @Transactional(readOnly = true)
    public List<TareaResponseDTO> listarPorPedido(
            Long pedidoId) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        pedidoRepository
                .findByIdPedidoAndEmpresaId(
                        pedidoId,
                        empresaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedido no encontrado"));

        List<Tarea> tareas =
                tareaRepository
                        .findByPedidoIdAndEmpresaId(
                                pedidoId,
                                empresaId);

        List<TareaResponseDTO> respuestas =
                new ArrayList<>();

        for (Tarea tarea : tareas) {
            respuestas.add(
                    convertirAResponse(tarea));
        }

        return respuestas;
    }

    @Transactional
    public TareaResponseDTO crear(
            TareaRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Pedido pedido =
                pedidoRepository
                        .findByIdPedidoAndEmpresaId(
                                dto.getPedidoId(),
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Pedido no encontrado"));

        if (dto.getResponsableId() != null) {

            Usuario responsable =
                    usuarioRepository
                            .findById(
                                    dto.getResponsableId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Responsable no encontrado"));

            if (!empresaId.equals(
                    responsable.getEmpresaId())) {

                throw new IllegalArgumentException(
                        "El responsable no pertenece a la empresa");
            }
        }

        String prioridad =
                normalizarPrioridad(
                        dto.getPrioridad());

        Tarea tarea =
                new Tarea();

        tarea.setEmpresaId(
                empresaId);

        tarea.setPedidoId(
                pedido.getIdPedido());

        tarea.setResponsableId(
                dto.getResponsableId());

        tarea.setTitulo(
                dto.getTitulo().trim());

        tarea.setDescripcion(
                dto.getDescripcion());

        tarea.setTipo(
                dto.getTipo()
                        .trim()
                        .toUpperCase());

        tarea.setPrioridad(
                prioridad);

        tarea.setEstado(
                "PENDIENTE");

        tarea.setFechaLimite(
                dto.getFechaLimite());

        tarea.setFechaCompletada(
                null);

        Tarea guardada =
                tareaRepository.save(tarea);

        return convertirAResponse(
                guardada);
    }

    @Transactional
    public TareaResponseDTO cambiarEstado(
            Long idTarea,
            TareaEstadoRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Tarea tarea =
                tareaRepository
                        .findByIdTareaAndEmpresaId(
                                idTarea,
                                usuario.getEmpresaId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Tarea no encontrada"));

        String estadoAnterior =
                tarea.getEstado();

        String nuevoEstado =
                dto.getEstado()
                        .trim()
                        .toUpperCase();

        if (!ESTADOS_VALIDOS.contains(
                nuevoEstado)) {

            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: "
                            + "PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA");
        }

        if (nuevoEstado.equals(
                estadoAnterior)) {

            throw new IllegalArgumentException(
                    "La tarea ya se encuentra en el estado "
                            + nuevoEstado);
        }

        tarea.setEstado(
                nuevoEstado);

        if ("COMPLETADA".equals(
                nuevoEstado)) {

            tarea.setFechaCompletada(
                    LocalDateTime.now());

        } else {

            tarea.setFechaCompletada(
                    null);
        }

        Tarea actualizada =
                tareaRepository.save(tarea);

        historialEstadoService.registrar(
                "TAREA",
                actualizada.getIdTarea(),
                estadoAnterior,
                nuevoEstado,
                "Cambio de estado de la tarea");

        return convertirAResponse(
                actualizada);
    }

    private String normalizarPrioridad(
            String prioridad) {

        if (prioridad == null
                || prioridad.isBlank()) {

            return "MEDIA";
        }

        String prioridadNormalizada =
                prioridad
                        .trim()
                        .toUpperCase();

        if (!PRIORIDADES_VALIDAS.contains(
                prioridadNormalizada)) {

            throw new IllegalArgumentException(
                    "Prioridad inválida. Valores permitidos: "
                            + "BAJA, MEDIA, ALTA, URGENTE");
        }

        return prioridadNormalizada;
    }

    private TareaResponseDTO convertirAResponse(
            Tarea tarea) {

        TareaResponseDTO response =
                new TareaResponseDTO();

        response.setIdTarea(
                tarea.getIdTarea());

        response.setEmpresaId(
                tarea.getEmpresaId());

        response.setPedidoId(
                tarea.getPedidoId());

        response.setResponsableId(
                tarea.getResponsableId());

        response.setTitulo(
                tarea.getTitulo());

        response.setDescripcion(
                tarea.getDescripcion());

        response.setTipo(
                tarea.getTipo());

        response.setPrioridad(
                tarea.getPrioridad());

        response.setEstado(
                tarea.getEstado());

        response.setFechaLimite(
                tarea.getFechaLimite());

        response.setFechaCompletada(
                tarea.getFechaCompletada());

        response.setFechaCreacion(
                tarea.getFechaCreacion());

        response.setFechaActualizacion(
                tarea.getFechaActualizacion());

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
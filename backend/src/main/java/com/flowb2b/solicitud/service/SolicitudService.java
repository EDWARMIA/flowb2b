package com.flowb2b.solicitud.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.cliente.entity.Cliente;
import com.flowb2b.cliente.repository.ClienteRepository;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.ProductoRepository;
import com.flowb2b.solicitud.dto.DetalleSolicitudRequestDTO;
import com.flowb2b.solicitud.dto.DetalleSolicitudResponseDTO;
import com.flowb2b.solicitud.dto.SolicitudRequestDTO;
import com.flowb2b.solicitud.dto.SolicitudResponseDTO;
import com.flowb2b.solicitud.entity.DetalleSolicitud;
import com.flowb2b.solicitud.entity.Solicitud;
import com.flowb2b.solicitud.repository.DetalleSolicitudRepository;
import com.flowb2b.solicitud.repository.SolicitudRepository;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public SolicitudService(
            SolicitudRepository solicitudRepository,
            DetalleSolicitudRepository detalleSolicitudRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository) {

        this.solicitudRepository = solicitudRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> listar() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId = usuario.getEmpresaId();

        List<Solicitud> solicitudes =
                solicitudRepository.findByEmpresaId(
                        empresaId
                );

        List<SolicitudResponseDTO> respuesta =
                new ArrayList<>();

        for (Solicitud solicitud : solicitudes) {

            SolicitudResponseDTO dto =
                    convertirAResponse(solicitud);

            respuesta.add(dto);
        }

        return respuesta;
    }

    @Transactional(readOnly = true)
    public SolicitudResponseDTO obtenerPorId(
            Long idSolicitud) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId = usuario.getEmpresaId();

        Solicitud solicitud =
                solicitudRepository
                        .findByIdSolicitudAndEmpresaId(
                                idSolicitud,
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Solicitud no encontrada"
                                )
                        );

        return convertirAResponse(solicitud);
    }

    @Transactional
    public SolicitudResponseDTO crear(
            SolicitudRequestDTO dto) {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        Long vendedorId =
                usuario.getIdUsuario();

        Cliente cliente =
                clienteRepository
                        .findByIdClienteAndEmpresaId(
                                dto.getClienteId(),
                                empresaId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cliente no encontrado"
                                )
                        );

        if (!Boolean.TRUE.equals(
                cliente.getEstado())) {

            throw new IllegalArgumentException(
                    "El cliente se encuentra inactivo"
            );
        }

        String codigo =
                dto.getCodigo().trim();

        boolean codigoExiste =
                solicitudRepository
                        .existsByEmpresaIdAndCodigo(
                                empresaId,
                                codigo
                        );

        if (codigoExiste) {

            throw new IllegalArgumentException(
                    "Ya existe una solicitud con ese código"
            );
        }

        validarProductos(
                dto.getDetalles(),
                empresaId
        );

        Solicitud solicitud =
                new Solicitud();

        solicitud.setEmpresaId(
                empresaId
        );

        solicitud.setClienteId(
                dto.getClienteId()
        );

        solicitud.setVendedorId(
                vendedorId
        );

        solicitud.setCodigo(
                codigo
        );

        solicitud.setOrigen(
                dto.getOrigen().trim()
        );

        solicitud.setDescripcion(
                dto.getDescripcion()
        );

        solicitud.setEstado(
                "NUEVA"
        );

        Solicitud solicitudGuardada =
                solicitudRepository.save(solicitud);

        for (DetalleSolicitudRequestDTO detalleDTO
                : dto.getDetalles()) {

            DetalleSolicitud detalle =
                    new DetalleSolicitud();

            detalle.setSolicitudId(
                    solicitudGuardada
                            .getIdSolicitud()
            );

            detalle.setProductoId(
                    detalleDTO.getProductoId()
            );

            detalle.setCantidad(
                    detalleDTO.getCantidad()
            );

            detalle.setObservacion(
                    detalleDTO.getObservacion()
            );

            detalleSolicitudRepository.save(
                    detalle
            );
        }

        return convertirAResponse(
                solicitudGuardada
        );
    }

    private void validarProductos(
            List<DetalleSolicitudRequestDTO> detalles,
            Long empresaId) {

        for (DetalleSolicitudRequestDTO detalle
                : detalles) {

            Producto producto =
                    productoRepository
                            .findByIdProductoAndEmpresaId(
                                    detalle.getProductoId(),
                                    empresaId
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Producto no encontrado: "
                                                    + detalle.getProductoId()
                                    )
                            );

            if (!Boolean.TRUE.equals(
                    producto.getEstado())) {

                throw new IllegalArgumentException(
                        "El producto "
                                + producto.getNombre()
                                + " se encuentra inactivo"
                );
            }
        }
    }

    private SolicitudResponseDTO convertirAResponse(
            Solicitud solicitud) {

        SolicitudResponseDTO response =
                new SolicitudResponseDTO();

        response.setIdSolicitud(
                solicitud.getIdSolicitud()
        );

        response.setEmpresaId(
                solicitud.getEmpresaId()
        );

        response.setClienteId(
                solicitud.getClienteId()
        );

        response.setVendedorId(
                solicitud.getVendedorId()
        );

        response.setCodigo(
                solicitud.getCodigo()
        );

        response.setOrigen(
                solicitud.getOrigen()
        );

        response.setDescripcion(
                solicitud.getDescripcion()
        );

        response.setEstado(
                solicitud.getEstado()
        );

        response.setFechaSolicitud(
                solicitud.getFechaSolicitud()
        );

        response.setFechaCreacion(
                solicitud.getFechaCreacion()
        );

        response.setFechaActualizacion(
                solicitud.getFechaActualizacion()
        );

        List<DetalleSolicitud> detalles =
                detalleSolicitudRepository
                        .findBySolicitudId(
                                solicitud.getIdSolicitud()
                        );

        List<DetalleSolicitudResponseDTO>
                detallesResponse =
                new ArrayList<>();

        for (DetalleSolicitud detalle : detalles) {

            DetalleSolicitudResponseDTO detalleResponse =
                    new DetalleSolicitudResponseDTO();

            detalleResponse.setIdDetalle(
                    detalle.getIdDetalle()
            );

            detalleResponse.setProductoId(
                    detalle.getProductoId()
            );

            detalleResponse.setCantidad(
                    detalle.getCantidad()
            );

            detalleResponse.setObservacion(
                    detalle.getObservacion()
            );

            detallesResponse.add(
                    detalleResponse
            );
        }

        response.setDetalles(
                detallesResponse
        );

        return response;
    }

    private UsuarioAutenticado obtenerUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "El usuario autenticado no es válido"
            );
        }

        return (UsuarioAutenticado) principal;
    }
}
package com.flowb2b.dashboard.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.aprobacion.repository.AprobacionRepository;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.cotizacion.repository.CotizacionRepository;
import com.flowb2b.dashboard.dto.DashboardResumenResponse;
import com.flowb2b.pedido.repository.PedidoRepository;
import com.flowb2b.solicitud.repository.SolicitudRepository;

@Service
public class DashboardService {

    private final SolicitudRepository solicitudRepository;

    private final CotizacionRepository cotizacionRepository;

    private final PedidoRepository pedidoRepository;

    private final AprobacionRepository aprobacionRepository;

    public DashboardService(
            SolicitudRepository solicitudRepository,
            CotizacionRepository cotizacionRepository,
            PedidoRepository pedidoRepository,
            AprobacionRepository aprobacionRepository) {

        this.solicitudRepository =
                solicitudRepository;

        this.cotizacionRepository =
                cotizacionRepository;

        this.pedidoRepository =
                pedidoRepository;

        this.aprobacionRepository =
                aprobacionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumenResponse obtenerResumen() {

        UsuarioAutenticado usuario =
                obtenerUsuarioAutenticado();

        Long empresaId =
                usuario.getEmpresaId();

        long solicitudes =
                solicitudRepository
                        .countByEmpresaId(
                                empresaId
                        );

        long cotizaciones =
                cotizacionRepository
                        .countByEmpresaId(
                                empresaId
                        );

        long pedidos =
                pedidoRepository
                        .countByEmpresaId(
                                empresaId
                        );

        long aprobacionesPendientes =
                aprobacionRepository
                        .countByEmpresaIdAndEstado(
                                empresaId,
                                "PENDIENTE"
                        );

        DashboardResumenResponse response =
                new DashboardResumenResponse();

        response.setSolicitudes(
                solicitudes
        );

        response.setCotizaciones(
                cotizaciones
        );

        response.setPedidos(
                pedidos
        );

        response.setAprobacionesPendientes(
                aprobacionesPendientes
        );

        return response;
    }

    private UsuarioAutenticado
            obtenerUsuarioAutenticado() {

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
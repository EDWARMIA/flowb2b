package com.flowb2b.DashboardGerencialController;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialAnaliticaDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialResumenDTO;
import com.flowb2b.DashboardGerencialResumenDTO.DashboardGerencialVentaMensualDTO;
import com.flowb2b.DashboardGerencialService.DashboardGerencialService;

@RestController
@RequestMapping(
        "/api/dashboard-gerencial"
)
public class DashboardGerencialController {

    private final DashboardGerencialService
            dashboardGerencialService;


    public DashboardGerencialController(
            DashboardGerencialService dashboardGerencialService) {

        this.dashboardGerencialService =
                dashboardGerencialService;
    }


    // =====================================================
    // RESUMEN
    // =====================================================

    @GetMapping(
            "/resumen"
    )
    @PreAuthorize(
            "hasAuthority('DASHBOARD_GERENCIAL_VER')"
    )
    public ResponseEntity<
            DashboardGerencialResumenDTO>
            obtenerResumen() {

        return ResponseEntity.ok(
                dashboardGerencialService
                        .obtenerResumen()
        );
    }


    // =====================================================
    // VENTAS MENSUALES
    // =====================================================

    @GetMapping(
            "/ventas-mensuales"
    )
    @PreAuthorize(
            "hasAuthority('DASHBOARD_GERENCIAL_VER')"
    )
    public ResponseEntity<
            List<DashboardGerencialVentaMensualDTO>>
            obtenerVentasMensuales() {

        return ResponseEntity.ok(
                dashboardGerencialService
                        .obtenerVentasMensuales()
        );
    }


    // =====================================================
    // ANALÍTICA GENERAL
    // =====================================================

    @GetMapping(
            "/analitica"
    )
    @PreAuthorize(
            "hasAuthority('DASHBOARD_GERENCIAL_VER')"
    )
    public ResponseEntity<
            DashboardGerencialAnaliticaDTO>
            obtenerAnalitica(

                    @RequestParam(
                            required = false
                    )
                    @DateTimeFormat(
                            iso =
                                    DateTimeFormat
                                            .ISO
                                            .DATE
                    )
                    LocalDate desde,

                    @RequestParam(
                            required = false
                    )
                    @DateTimeFormat(
                            iso =
                                    DateTimeFormat
                                            .ISO
                                            .DATE
                    )
                    LocalDate hasta,

                    @RequestParam(
                            required = false
                    )
                    Long clienteId,

                    @RequestParam(
                            required = false
                    )
                    Long productoId,

                    @RequestParam(
                            required = false
                    )
                    Long vendedorId) {


        DashboardGerencialAnaliticaDTO response =
                dashboardGerencialService
                        .obtenerAnalitica(
                                desde,
                                hasta,
                                clienteId,
                                productoId,
                                vendedorId
                        );


        return ResponseEntity.ok(
                response
        );
    }
}
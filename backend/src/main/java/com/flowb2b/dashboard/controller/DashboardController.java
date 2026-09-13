package com.flowb2b.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowb2b.dashboard.dto.DashboardResumenResponse;
import com.flowb2b.dashboard.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService =
                dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenResponse>
            obtenerResumen() {

        DashboardResumenResponse respuesta =
                dashboardService.obtenerResumen();

        return ResponseEntity.ok(
                respuesta
        );
    }
}
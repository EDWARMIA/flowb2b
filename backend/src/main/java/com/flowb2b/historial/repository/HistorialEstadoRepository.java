package com.flowb2b.historial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.historial.entity.HistorialEstado;

@Repository
public interface HistorialEstadoRepository
        extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByEmpresaIdOrderByFechaDesc(
            Long empresaId);

    List<HistorialEstado> findByEmpresaIdAndTipoEntidadAndEntidadIdOrderByFechaDesc(
            Long empresaId,
            String tipoEntidad,
            Long entidadId);
}
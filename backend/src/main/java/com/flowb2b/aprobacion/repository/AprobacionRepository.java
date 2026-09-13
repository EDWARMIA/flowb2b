package com.flowb2b.aprobacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.aprobacion.entity.Aprobacion;

@Repository
public interface AprobacionRepository
        extends JpaRepository<Aprobacion, Long> {

    List<Aprobacion> findByEmpresaId(
            Long empresaId
    );

    List<Aprobacion> findByEmpresaIdAndEstado(
            Long empresaId,
            String estado
    );

    Optional<Aprobacion> findByIdAprobacionAndEmpresaId(
            Long idAprobacion,
            Long empresaId
    );

    Optional<Aprobacion> findByCotizacionIdAndEmpresaIdAndEstado(
            Long cotizacionId,
            Long empresaId,
            String estado
    );

    boolean existsByCotizacionIdAndEmpresaIdAndEstado(
            Long cotizacionId,
            Long empresaId,
            String estado
    );

    long countByEmpresaIdAndEstado(
            Long empresaId,
            String estado
    );
}
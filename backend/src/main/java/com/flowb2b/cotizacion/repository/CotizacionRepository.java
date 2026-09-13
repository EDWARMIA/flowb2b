package com.flowb2b.cotizacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.cotizacion.entity.Cotizacion;

@Repository
public interface CotizacionRepository
        extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEmpresaId(
            Long empresaId
    );

    Optional<Cotizacion> findByIdCotizacionAndEmpresaId(
            Long idCotizacion,
            Long empresaId
    );

    List<Cotizacion> findBySolicitudIdAndEmpresaId(
            Long solicitudId,
            Long empresaId
    );

    boolean existsByEmpresaIdAndCodigo(
            Long empresaId,
            String codigo
    );

    long countByEmpresaId(
            Long empresaId
    );
}
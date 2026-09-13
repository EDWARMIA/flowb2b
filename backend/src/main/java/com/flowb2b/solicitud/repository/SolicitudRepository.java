package com.flowb2b.solicitud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.solicitud.entity.Solicitud;

@Repository
public interface SolicitudRepository
        extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEmpresaId(
            Long empresaId
    );

    Optional<Solicitud> findByIdSolicitudAndEmpresaId(
            Long idSolicitud,
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
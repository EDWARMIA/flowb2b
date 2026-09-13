package com.flowb2b.solicitud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.solicitud.entity.DetalleSolicitud;

@Repository
public interface DetalleSolicitudRepository
        extends JpaRepository<DetalleSolicitud, Long> {

    List<DetalleSolicitud> findBySolicitudId(
            Long solicitudId
    );

    void deleteBySolicitudId(
            Long solicitudId
    );
}
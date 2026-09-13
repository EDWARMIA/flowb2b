package com.flowb2b.cotizacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.cotizacion.entity.DetalleCotizacion;

@Repository
public interface DetalleCotizacionRepository
        extends JpaRepository<DetalleCotizacion, Long> {

    List<DetalleCotizacion> findByCotizacionId(
            Long cotizacionId
    );

    void deleteByCotizacionId(
            Long cotizacionId
    );
}
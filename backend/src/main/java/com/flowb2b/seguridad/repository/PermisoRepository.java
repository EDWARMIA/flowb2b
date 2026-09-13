package com.flowb2b.seguridad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.seguridad.entity.Permiso;

@Repository
public interface PermisoRepository
        extends JpaRepository<Permiso, Long> {

    List<Permiso> findAllByOrderByCodigoAsc();
}
package com.flowb2b.seguridad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.seguridad.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    List<Rol> findByEmpresaIdOrderByNombreAsc(Long empresaId);

    Optional<Rol> findByIdRolAndEmpresaId(
            Long idRol,
            Long empresaId
    );

    boolean existsByEmpresaIdAndNombreIgnoreCase(
            Long empresaId,
            String nombre
    );
}
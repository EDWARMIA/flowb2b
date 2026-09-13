package com.flowb2b.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flowb2b.inventario.entity.Inventario;

import jakarta.persistence.LockModeType;

@Repository
public interface InventarioRepository
        extends JpaRepository<Inventario, Long> {

    List<Inventario> findByEmpresaId(
            Long empresaId
    );

    Optional<Inventario> findByIdInventarioAndEmpresaId(
            Long idInventario,
            Long empresaId
    );

    Optional<Inventario> findByProductoIdAndEmpresaId(
            Long productoId,
            Long empresaId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.productoId = :productoId
          AND i.empresaId = :empresaId
    """)
    Optional<Inventario> buscarPorProductoYEmpresaConBloqueo(
            @Param("productoId") Long productoId,
            @Param("empresaId") Long empresaId
    );

    boolean existsByProductoIdAndEmpresaId(
            Long productoId,
            Long empresaId
    );
}
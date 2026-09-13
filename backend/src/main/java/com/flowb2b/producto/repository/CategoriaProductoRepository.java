package com.flowb2b.producto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.producto.entity.CategoriaProducto;

@Repository
public interface CategoriaProductoRepository
        extends JpaRepository<CategoriaProducto, Long> {

    List<CategoriaProducto> findByEmpresaId(
            Long empresaId
    );

    Optional<CategoriaProducto> findByIdCategoriaAndEmpresaId(
            Long idCategoria,
            Long empresaId
    );

    boolean existsByEmpresaIdAndNombre(
            Long empresaId,
            String nombre
    );

    boolean existsByEmpresaIdAndNombreAndIdCategoriaNot(
            Long empresaId,
            String nombre,
            Long idCategoria
    );
}
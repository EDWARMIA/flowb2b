package com.flowb2b.producto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.producto.entity.Producto;

@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, Long> {

    List<Producto> findByEmpresaId(
            Long empresaId
    );

    Optional<Producto> findByIdProductoAndEmpresaId(
            Long idProducto,
            Long empresaId
    );

    List<Producto> findByCategoriaIdAndEmpresaId(
            Long categoriaId,
            Long empresaId
    );

    boolean existsByEmpresaIdAndCodigo(
            Long empresaId,
            String codigo
    );

    boolean existsByEmpresaIdAndCodigoAndIdProductoNot(
            Long empresaId,
            String codigo,
            Long idProducto
    );
}
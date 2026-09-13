package com.flowb2b.pedido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.pedido.entity.Pedido;

@Repository
public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEmpresaId(
            Long empresaId
    );

    Optional<Pedido> findByIdPedidoAndEmpresaId(
            Long idPedido,
            Long empresaId
    );

    Optional<Pedido> findByCotizacionIdAndEmpresaId(
            Long cotizacionId,
            Long empresaId
    );

    boolean existsByCotizacionIdAndEmpresaId(
            Long cotizacionId,
            Long empresaId
    );

    boolean existsByCodigoAndEmpresaId(
            String codigo,
            Long empresaId
    );

    long countByEmpresaId(
            Long empresaId
    );
}
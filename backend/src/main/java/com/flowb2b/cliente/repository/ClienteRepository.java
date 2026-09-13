package com.flowb2b.cliente.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.cliente.entity.Cliente;

@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmpresaId(
            Long empresaId
    );

    Optional<Cliente> findByIdClienteAndEmpresaId(
            Long idCliente,
            Long empresaId
    );

    boolean existsByEmpresaIdAndNumeroDocumento(
            Long empresaId,
            String numeroDocumento
    );
}
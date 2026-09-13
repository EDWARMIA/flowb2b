package com.flowb2b.auditoria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.auditoria.entity.Auditoria;

@Repository
public interface AuditoriaRepository
        extends JpaRepository<Auditoria, Long> {

    List<Auditoria>
            findByEmpresaIdOrderByFechaDesc(
                    Long empresaId);

    List<Auditoria>
            findByEmpresaIdAndEntidadAndEntidadIdOrderByFechaDesc(
                    Long empresaId,
                    String entidad,
                    Long entidadId);

    List<Auditoria>
            findByEmpresaIdAndUsuarioIdOrderByFechaDesc(
                    Long empresaId,
                    Long usuarioId);

    List<Auditoria>
            findByEmpresaIdAndAccionOrderByFechaDesc(
                    Long empresaId,
                    String accion);
}
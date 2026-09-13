package com.flowb2b.notificacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.notificacion.entity.Notificacion;

@Repository
public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdAndEmpresaIdOrderByFechaCreacionDesc(
            Long usuarioId,
            Long empresaId);

    List<Notificacion> findByUsuarioIdAndEmpresaIdAndLeidaOrderByFechaCreacionDesc(
            Long usuarioId,
            Long empresaId,
            Boolean leida);

    Optional<Notificacion> findByIdNotificacionAndUsuarioIdAndEmpresaId(
            Long idNotificacion,
            Long usuarioId,
            Long empresaId);
}
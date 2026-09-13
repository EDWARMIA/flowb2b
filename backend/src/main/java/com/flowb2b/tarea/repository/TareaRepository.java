package com.flowb2b.tarea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.tarea.entity.Tarea;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByEmpresaId(Long empresaId);

    Optional<Tarea> findByIdTareaAndEmpresaId(
            Long idTarea,
            Long empresaId);

    List<Tarea> findByPedidoIdAndEmpresaId(
            Long pedidoId,
            Long empresaId);

    List<Tarea> findByResponsableIdAndEmpresaId(
            Long responsableId,
            Long empresaId);

    List<Tarea> findByEstadoAndEmpresaId(
            String estado,
            Long empresaId);
}
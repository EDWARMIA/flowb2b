package com.flowb2b.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowb2b.usuario.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByEmpresaIdOrderByNombreAscApellidoAsc(Long empresaId);

    Optional<Usuario> findByIdUsuarioAndEmpresaId(
            Long idUsuario,
            Long empresaId
    );
}
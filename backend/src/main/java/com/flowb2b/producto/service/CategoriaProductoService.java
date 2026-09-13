package com.flowb2b.producto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.producto.dto.CategoriaProductoRequestDTO;
import com.flowb2b.producto.dto.CategoriaProductoResponseDTO;
import com.flowb2b.producto.entity.CategoriaProducto;
import com.flowb2b.producto.repository.CategoriaProductoRepository;

@Service
public class CategoriaProductoService {

    private final CategoriaProductoRepository categoriaRepository;

    public CategoriaProductoService(
            CategoriaProductoRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaProductoResponseDTO> listarCategorias() {

        Long empresaId = obtenerEmpresaIdAutenticada();

        return categoriaRepository
                .findByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaProductoResponseDTO buscarPorId(
            Long idCategoria) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        CategoriaProducto categoria = categoriaRepository
                .findByIdCategoriaAndEmpresaId(
                        idCategoria,
                        empresaId
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Categoría no encontrada"
                    )
                );

        return convertirAResponseDTO(categoria);
    }

    @Transactional
    public CategoriaProductoResponseDTO crearCategoria(
            CategoriaProductoRequestDTO dto) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        validarNombreDuplicado(
                empresaId,
                dto.getNombre()
        );

        CategoriaProducto categoria =
                new CategoriaProducto();

        categoria.setEmpresaId(empresaId);
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion());

        categoria.setEstado(
                dto.getEstado() != null
                        ? dto.getEstado()
                        : true
        );

        CategoriaProducto guardada =
                categoriaRepository.save(categoria);

        return convertirAResponseDTO(guardada);
    }

    @Transactional
    public CategoriaProductoResponseDTO actualizarCategoria(
            Long idCategoria,
            CategoriaProductoRequestDTO dto) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        CategoriaProducto categoria = categoriaRepository
                .findByIdCategoriaAndEmpresaId(
                        idCategoria,
                        empresaId
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Categoría no encontrada"
                    )
                );

        boolean nombreDuplicado =
                categoriaRepository
                    .existsByEmpresaIdAndNombreAndIdCategoriaNot(
                            empresaId,
                            dto.getNombre(),
                            idCategoria
                    );

        if (nombreDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con el nombre "
                    + dto.getNombre()
            );
        }

        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion());

        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }

        CategoriaProducto actualizada =
                categoriaRepository.save(categoria);

        return convertirAResponseDTO(actualizada);
    }

    private void validarNombreDuplicado(
            Long empresaId,
            String nombre) {

        boolean existe =
                categoriaRepository
                    .existsByEmpresaIdAndNombre(
                            empresaId,
                            nombre
                    );

        if (existe) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con el nombre "
                    + nombre
            );
        }
    }

    private Long obtenerEmpresaIdAutenticada() {

        Authentication authentication =
                SecurityContextHolder
                    .getContext()
                    .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No existe un usuario autenticado"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UsuarioAutenticado)) {
            throw new IllegalStateException(
                    "No se pudo obtener la información del usuario autenticado"
            );
        }

        UsuarioAutenticado usuario =
                (UsuarioAutenticado) principal;

        return usuario.getEmpresaId();
    }

    private CategoriaProductoResponseDTO convertirAResponseDTO(
            CategoriaProducto categoria) {

        CategoriaProductoResponseDTO dto =
                new CategoriaProductoResponseDTO();

        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setEmpresaId(categoria.getEmpresaId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setEstado(categoria.getEstado());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        dto.setFechaActualizacion(
                categoria.getFechaActualizacion()
        );

        return dto;
    }
}
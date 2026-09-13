package com.flowb2b.producto.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.inventario.entity.Inventario;
import com.flowb2b.inventario.repository.InventarioRepository;
import com.flowb2b.producto.dto.ProductoRequestDTO;
import com.flowb2b.producto.dto.ProductoResponseDTO;
import com.flowb2b.producto.entity.CategoriaProducto;
import com.flowb2b.producto.entity.Producto;
import com.flowb2b.producto.repository.CategoriaProductoRepository;
import com.flowb2b.producto.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final InventarioRepository inventarioRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaProductoRepository categoriaRepository,
            InventarioRepository inventarioRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos() {

        Long empresaId = obtenerEmpresaIdAutenticada();

        return productoRepository
                .findByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(
            Long idProducto) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        Producto producto = productoRepository
                .findByIdProductoAndEmpresaId(
                        idProducto,
                        empresaId
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Producto no encontrado"
                    )
                );

        return convertirAResponseDTO(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(
            Long idCategoria) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        validarCategoriaPerteneceAEmpresa(
                idCategoria,
                empresaId
        );

        return productoRepository
                .findByCategoriaIdAndEmpresaId(
                        idCategoria,
                        empresaId
                )
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponseDTO crearProducto(
            ProductoRequestDTO dto) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        validarCategoriaPerteneceAEmpresa(
                dto.getCategoriaId(),
                empresaId
        );

        validarCodigoDuplicado(
                empresaId,
                dto.getCodigo()
        );

        Producto producto = new Producto();

        producto.setEmpresaId(empresaId);
        producto.setCategoriaId(dto.getCategoriaId());
        producto.setCodigo(dto.getCodigo().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(
                dto.getUnidadMedida().trim()
        );
        producto.setPrecioBase(dto.getPrecioBase());

        producto.setEstado(
                dto.getEstado() != null
                        ? dto.getEstado()
                        : true
        );

        Producto productoGuardado =
                productoRepository.save(producto);

        crearInventarioInicial(
                productoGuardado,
                empresaId
        );

        return convertirAResponseDTO(productoGuardado);
    }

    @Transactional
    public ProductoResponseDTO actualizarProducto(
            Long idProducto,
            ProductoRequestDTO dto) {

        Long empresaId = obtenerEmpresaIdAutenticada();

        Producto producto = productoRepository
                .findByIdProductoAndEmpresaId(
                        idProducto,
                        empresaId
                )
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Producto no encontrado"
                    )
                );

        validarCategoriaPerteneceAEmpresa(
                dto.getCategoriaId(),
                empresaId
        );

        boolean codigoDuplicado =
                productoRepository
                    .existsByEmpresaIdAndCodigoAndIdProductoNot(
                            empresaId,
                            dto.getCodigo(),
                            idProducto
                    );

        if (codigoDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con el código "
                    + dto.getCodigo()
            );
        }

        producto.setCategoriaId(dto.getCategoriaId());
        producto.setCodigo(dto.getCodigo().trim());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion());
        producto.setUnidadMedida(
                dto.getUnidadMedida().trim()
        );
        producto.setPrecioBase(dto.getPrecioBase());

        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }

        Producto actualizado =
                productoRepository.save(producto);

        return convertirAResponseDTO(actualizado);
    }

    private void validarCategoriaPerteneceAEmpresa(
            Long idCategoria,
            Long empresaId) {

        CategoriaProducto categoria =
                categoriaRepository
                    .findByIdCategoriaAndEmpresaId(
                            idCategoria,
                            empresaId
                    )
                    .orElseThrow(() ->
                        new ResourceNotFoundException(
                            "Categoría no encontrada"
                        )
                    );

        if (!Boolean.TRUE.equals(categoria.getEstado())) {
            throw new IllegalArgumentException(
                    "La categoría seleccionada está inactiva"
            );
        }
    }

    private void validarCodigoDuplicado(
            Long empresaId,
            String codigo) {

        boolean existe =
                productoRepository
                    .existsByEmpresaIdAndCodigo(
                            empresaId,
                            codigo
                    );

        if (existe) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con el código "
                    + codigo
            );
        }
    }

    private void crearInventarioInicial(
            Producto producto,
            Long empresaId) {

        Inventario inventario = new Inventario();

        inventario.setEmpresaId(empresaId);
        inventario.setProductoId(
                producto.getIdProducto()
        );

        inventario.setStockActual(
                BigDecimal.ZERO
        );

        inventario.setStockReservado(
                BigDecimal.ZERO
        );

        inventario.setStockMinimo(
                BigDecimal.ZERO
        );

        inventarioRepository.save(inventario);
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

    private ProductoResponseDTO convertirAResponseDTO(
            Producto producto) {

        ProductoResponseDTO dto =
                new ProductoResponseDTO();

        dto.setIdProducto(
                producto.getIdProducto()
        );

        dto.setEmpresaId(
                producto.getEmpresaId()
        );

        dto.setCategoriaId(
                producto.getCategoriaId()
        );

        dto.setCodigo(
                producto.getCodigo()
        );

        dto.setNombre(
                producto.getNombre()
        );

        dto.setDescripcion(
                producto.getDescripcion()
        );

        dto.setUnidadMedida(
                producto.getUnidadMedida()
        );

        dto.setPrecioBase(
                producto.getPrecioBase()
        );

        dto.setEstado(
                producto.getEstado()
        );

        dto.setFechaCreacion(
                producto.getFechaCreacion()
        );

        dto.setFechaActualizacion(
                producto.getFechaActualizacion()
        );

        return dto;
    }
}
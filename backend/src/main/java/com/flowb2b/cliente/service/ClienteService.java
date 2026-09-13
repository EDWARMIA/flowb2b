package com.flowb2b.cliente.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.auditoria.service.AuditoriaService;
import com.flowb2b.auth.security.UsuarioAutenticado;
import com.flowb2b.cliente.dto.ClienteRequestDTO;
import com.flowb2b.cliente.dto.ClienteResponseDTO;
import com.flowb2b.cliente.entity.Cliente;
import com.flowb2b.cliente.repository.ClienteRepository;
import com.flowb2b.common.exception.ResourceNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(
            ClienteRepository clienteRepository,
            AuditoriaService auditoriaService) {

        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    // =====================================================
    // LISTAR CLIENTES
    // =====================================================

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientes() {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        return clienteRepository
                .findByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // BUSCAR CLIENTE POR ID
    // =====================================================

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(
            Long idCliente) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        Cliente cliente =
                clienteRepository
                        .findByIdClienteAndEmpresaId(
                                idCliente,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cliente no encontrado"));

        return convertirAResponseDTO(cliente);
    }

    // =====================================================
    // CREAR CLIENTE
    // =====================================================

    @Transactional
    public ClienteResponseDTO crearCliente(
            ClienteRequestDTO dto) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        validarDocumentoDuplicado(
                empresaId,
                dto.getNumeroDocumento());

        Cliente cliente =
                new Cliente();

        // empresaId SIEMPRE viene del usuario autenticado.
        cliente.setEmpresaId(
                empresaId);

        cliente.setTipoDocumento(
                dto.getTipoDocumento());

        cliente.setNumeroDocumento(
                dto.getNumeroDocumento());

        cliente.setRazonSocial(
                dto.getRazonSocial());

        cliente.setNombreComercial(
                dto.getNombreComercial());

        cliente.setCorreo(
                dto.getCorreo());

        cliente.setTelefono(
                dto.getTelefono());

        cliente.setDireccion(
                dto.getDireccion());

        cliente.setContactoNombre(
                dto.getContactoNombre());

        cliente.setContactoTelefono(
                dto.getContactoTelefono());

        cliente.setContactoCorreo(
                dto.getContactoCorreo());

        cliente.setEstado(
                dto.getEstado() != null
                        ? dto.getEstado()
                        : true);

        Cliente clienteGuardado =
                clienteRepository.save(cliente);

        // =================================================
        // AUDITORÍA - CREACIÓN
        // =================================================

        auditoriaService.registrar(
                "CREAR",
                "CLIENTE",
                clienteGuardado.getIdCliente(),
                null,
                construirJsonCliente(clienteGuardado),
                null);

        return convertirAResponseDTO(
                clienteGuardado);
    }

    // =====================================================
    // ACTUALIZAR CLIENTE
    // =====================================================

    @Transactional
    public ClienteResponseDTO actualizarCliente(
            Long idCliente,
            ClienteRequestDTO dto) {

        Long empresaId =
                obtenerEmpresaIdAutenticada();

        Cliente cliente =
                clienteRepository
                        .findByIdClienteAndEmpresaId(
                                idCliente,
                                empresaId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cliente no encontrado"));

        /*
         * IMPORTANTE:
         * capturamos los datos ANTES de modificar la entidad.
         */
        String datosAnteriores =
                construirJsonCliente(cliente);

        /*
         * Si cambia el documento, comprobamos que el nuevo
         * no pertenezca a otro cliente de esta empresa.
         */
        if (dto.getNumeroDocumento() != null
                && !dto.getNumeroDocumento().isBlank()
                && !dto.getNumeroDocumento().equals(
                        cliente.getNumeroDocumento())) {

            validarDocumentoDuplicado(
                    empresaId,
                    dto.getNumeroDocumento());
        }

        cliente.setTipoDocumento(
                dto.getTipoDocumento());

        cliente.setNumeroDocumento(
                dto.getNumeroDocumento());

        cliente.setRazonSocial(
                dto.getRazonSocial());

        cliente.setNombreComercial(
                dto.getNombreComercial());

        cliente.setCorreo(
                dto.getCorreo());

        cliente.setTelefono(
                dto.getTelefono());

        cliente.setDireccion(
                dto.getDireccion());

        cliente.setContactoNombre(
                dto.getContactoNombre());

        cliente.setContactoTelefono(
                dto.getContactoTelefono());

        cliente.setContactoCorreo(
                dto.getContactoCorreo());

        if (dto.getEstado() != null) {

            cliente.setEstado(
                    dto.getEstado());
        }

        /*
         * empresaId NO se modifica.
         * Un cliente no puede cambiar de empresa.
         */

        Cliente clienteActualizado =
                clienteRepository.save(cliente);

        // =================================================
        // AUDITORÍA - ACTUALIZACIÓN
        // =================================================

        String datosNuevos =
                construirJsonCliente(
                        clienteActualizado);

        auditoriaService.registrar(
                "ACTUALIZAR",
                "CLIENTE",
                clienteActualizado.getIdCliente(),
                datosAnteriores,
                datosNuevos,
                null);

        return convertirAResponseDTO(
                clienteActualizado);
    }

    // =====================================================
    // CONSTRUIR JSON PARA AUDITORÍA
    // =====================================================

    private String construirJsonCliente(
            Cliente cliente) {

        return "{"
                + "\"tipoDocumento\":"
                + valorJson(cliente.getTipoDocumento())
                + ","
                + "\"numeroDocumento\":"
                + valorJson(cliente.getNumeroDocumento())
                + ","
                + "\"razonSocial\":"
                + valorJson(cliente.getRazonSocial())
                + ","
                + "\"nombreComercial\":"
                + valorJson(cliente.getNombreComercial())
                + ","
                + "\"correo\":"
                + valorJson(cliente.getCorreo())
                + ","
                + "\"telefono\":"
                + valorJson(cliente.getTelefono())
                + ","
                + "\"direccion\":"
                + valorJson(cliente.getDireccion())
                + ","
                + "\"contactoNombre\":"
                + valorJson(cliente.getContactoNombre())
                + ","
                + "\"contactoTelefono\":"
                + valorJson(cliente.getContactoTelefono())
                + ","
                + "\"contactoCorreo\":"
                + valorJson(cliente.getContactoCorreo())
                + ","
                + "\"estado\":"
                + cliente.getEstado()
                + "}";
    }

    // =====================================================
    // ESCAPAR TEXTO PARA JSON
    // =====================================================

    private String valorJson(
            String valor) {

        if (valor == null) {
            return "null";
        }

        String escapado =
                valor
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t");

        return "\""
                + escapado
                + "\"";
    }

    // =====================================================
    // VALIDAR DOCUMENTO DUPLICADO
    // =====================================================

    private void validarDocumentoDuplicado(
            Long empresaId,
            String numeroDocumento) {

        if (numeroDocumento == null
                || numeroDocumento.isBlank()) {

            return;
        }

        boolean existe =
                clienteRepository
                        .existsByEmpresaIdAndNumeroDocumento(
                                empresaId,
                                numeroDocumento);

        if (existe) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con el número de documento "
                            + numeroDocumento);
        }
    }

    // =====================================================
    // EMPRESA DEL USUARIO AUTENTICADO
    // =====================================================

    private Long obtenerEmpresaIdAutenticada() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado");
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof UsuarioAutenticado)) {

            throw new IllegalStateException(
                    "No se pudo obtener la información "
                            + "del usuario autenticado");
        }

        UsuarioAutenticado usuario =
                (UsuarioAutenticado) principal;

        return usuario.getEmpresaId();
    }

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================

    private ClienteResponseDTO convertirAResponseDTO(
            Cliente cliente) {

        ClienteResponseDTO dto =
                new ClienteResponseDTO();

        dto.setIdCliente(
                cliente.getIdCliente());

        dto.setEmpresaId(
                cliente.getEmpresaId());

        dto.setTipoDocumento(
                cliente.getTipoDocumento());

        dto.setNumeroDocumento(
                cliente.getNumeroDocumento());

        dto.setRazonSocial(
                cliente.getRazonSocial());

        dto.setNombreComercial(
                cliente.getNombreComercial());

        dto.setCorreo(
                cliente.getCorreo());

        dto.setTelefono(
                cliente.getTelefono());

        dto.setDireccion(
                cliente.getDireccion());

        dto.setContactoNombre(
                cliente.getContactoNombre());

        dto.setContactoTelefono(
                cliente.getContactoTelefono());

        dto.setContactoCorreo(
                cliente.getContactoCorreo());

        dto.setEstado(
                cliente.getEstado());

        dto.setFechaCreacion(
                cliente.getFechaCreacion());

        dto.setFechaActualizacion(
                cliente.getFechaActualizacion());

        return dto;
    }
}
package com.flowb2b.empresa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowb2b.common.exception.ResourceNotFoundException;
import com.flowb2b.empresa.dto.EmpresaRequestDTO;
import com.flowb2b.empresa.dto.EmpresaResponseDTO;
import com.flowb2b.empresa.entity.Empresa;
import com.flowb2b.empresa.repository.EmpresaRepository;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponseDTO> listarEmpresas() {
        return empresaRepository.findAll()
                .stream()
                .map(this::convertirResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarPorId(Long id) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Empresa no encontrada con ID: " + id
                    )
                );

        return convertirResponseDTO(empresa);
    }

    @Transactional
    public EmpresaResponseDTO crearEmpresa(EmpresaRequestDTO dto) {

        if (empresaRepository.existsByRuc(dto.getRuc())) {
            throw new IllegalArgumentException(
                "Ya existe una empresa registrada con el RUC: " + dto.getRuc()
            );
        }

        Empresa empresa = new Empresa();

        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setNombreComercial(dto.getNombreComercial());
        empresa.setRuc(dto.getRuc());
        empresa.setCorreo(dto.getCorreo());
        empresa.setTelefono(dto.getTelefono());
        empresa.setDireccion(dto.getDireccion());

        if (dto.getEstado() != null) {
            empresa.setEstado(dto.getEstado());
        }

        Empresa empresaGuardada = empresaRepository.save(empresa);

        return convertirResponseDTO(empresaGuardada);
    }

    @Transactional
    public EmpresaResponseDTO actualizarEmpresa(
            Long id,
            EmpresaRequestDTO dto) {

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Empresa no encontrada con ID: " + id
                    )
                );

        if (!empresa.getRuc().equals(dto.getRuc())
                && empresaRepository.existsByRuc(dto.getRuc())) {

            throw new IllegalArgumentException(
                "Ya existe una empresa registrada con el RUC: " + dto.getRuc()
            );
        }

        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setNombreComercial(dto.getNombreComercial());
        empresa.setRuc(dto.getRuc());
        empresa.setCorreo(dto.getCorreo());
        empresa.setTelefono(dto.getTelefono());
        empresa.setDireccion(dto.getDireccion());

        if (dto.getEstado() != null) {
            empresa.setEstado(dto.getEstado());
        }

        Empresa empresaActualizada = empresaRepository.save(empresa);

        return convertirResponseDTO(empresaActualizada);
    }

    private EmpresaResponseDTO convertirResponseDTO(Empresa empresa) {

        EmpresaResponseDTO dto = new EmpresaResponseDTO();

        dto.setIdEmpresa(empresa.getIdEmpresa());
        dto.setRazonSocial(empresa.getRazonSocial());
        dto.setNombreComercial(empresa.getNombreComercial());
        dto.setRuc(empresa.getRuc());
        dto.setCorreo(empresa.getCorreo());
        dto.setTelefono(empresa.getTelefono());
        dto.setDireccion(empresa.getDireccion());
        dto.setEstado(empresa.getEstado());
        dto.setFechaCreacion(empresa.getFechaCreacion());
        dto.setFechaActualizacion(empresa.getFechaActualizacion());

        return dto;
    }
}
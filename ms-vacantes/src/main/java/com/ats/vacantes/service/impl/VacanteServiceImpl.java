package com.ats.vacantes.service.impl;

import com.ats.vacantes.dto.VacanteRequest;
import com.ats.vacantes.dto.VacanteResponse;
import com.ats.vacantes.entity.EstadoVacante;
import com.ats.vacantes.entity.Vacante;
import com.ats.vacantes.exception.ResourceNotFoundException;
import com.ats.vacantes.repository.VacanteRepository;
import com.ats.vacantes.service.VacanteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VacanteServiceImpl implements VacanteService {

    private final VacanteRepository vacanteRepository;

    public VacanteServiceImpl(VacanteRepository vacanteRepository) {
        this.vacanteRepository = vacanteRepository;
    }

    @Override
    @Transactional
    public VacanteResponse crear(VacanteRequest request) {
        log.info("Creando vacante '{}'", request.getTitulo());
        Vacante vacante = Vacante.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .requisitos(request.getRequisitos())
                .salario(request.getSalario())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoVacante.ABIERTA)
                .fechaPublicacion(LocalDateTime.now())
                .build();

        Vacante guardada = vacanteRepository.save(vacante);
        log.info("Vacante creada con id {}", guardada.getId());
        return toResponse(guardada);
    }

    @Override
    @Transactional
    public VacanteResponse actualizar(Long id, VacanteRequest request) {
        log.info("Actualizando vacante con id {}", id);
        Vacante vacante = vacanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacante con id " + id + " no encontrada"));

        vacante.setTitulo(request.getTitulo());
        vacante.setDescripcion(request.getDescripcion());
        vacante.setRequisitos(request.getRequisitos());
        vacante.setSalario(request.getSalario());
        if (request.getEstado() != null) {
            vacante.setEstado(request.getEstado());
        }

        Vacante actualizada = vacanteRepository.save(vacante);
        log.info("Vacante {} actualizada", id);
        return toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando vacante con id {}", id);
        if (!vacanteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vacante con id " + id + " no encontrada");
        }
        vacanteRepository.deleteById(id);
        log.info("Vacante {} eliminada", id);
    }

    @Override
    public VacanteResponse obtenerPorId(Long id) {
        log.info("Consultando vacante con id {}", id);
        Vacante vacante = vacanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacante con id " + id + " no encontrada"));
        return toResponse(vacante);
    }

    @Override
    public List<VacanteResponse> listarTodas() {
        log.info("Listando todas las vacantes");
        return vacanteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<VacanteResponse> listarAbiertas() {
        log.info("Listando vacantes abiertas");
        return vacanteRepository.findByEstado(EstadoVacante.ABIERTA).stream().map(this::toResponse).toList();
    }

    private VacanteResponse toResponse(Vacante vacante) {
        return VacanteResponse.builder()
                .id(vacante.getId())
                .titulo(vacante.getTitulo())
                .descripcion(vacante.getDescripcion())
                .requisitos(vacante.getRequisitos())
                .salario(vacante.getSalario())
                .estado(vacante.getEstado())
                .fechaPublicacion(vacante.getFechaPublicacion())
                .build();
    }
}

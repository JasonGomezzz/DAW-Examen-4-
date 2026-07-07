package com.ats.postulaciones.service.impl;

import com.ats.postulaciones.dto.EstadoPostulacionRequest;
import com.ats.postulaciones.dto.PostulacionRequest;
import com.ats.postulaciones.dto.PostulacionResponse;
import com.ats.postulaciones.entity.EstadoPostulacion;
import com.ats.postulaciones.entity.Postulacion;
import com.ats.postulaciones.exception.ResourceNotFoundException;
import com.ats.postulaciones.repository.PostulacionRepository;
import com.ats.postulaciones.service.PostulacionService;
import com.ats.postulaciones.service.ValidacionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PostulacionServiceImpl implements PostulacionService {

    private final PostulacionRepository postulacionRepository;
    private final ValidacionService validacionService;

    public PostulacionServiceImpl(PostulacionRepository postulacionRepository, ValidacionService validacionService) {
        this.postulacionRepository = postulacionRepository;
        this.validacionService = validacionService;
    }

    @Override
    @Transactional
    public PostulacionResponse crear(PostulacionRequest request) {
        log.info("Creando postulación de usuario {} a vacante {}", request.getIdUsuario(), request.getIdVacante());

        validacionService.validarVacante(request.getIdVacante());
        validacionService.validarUsuario(request.getIdUsuario());

        Postulacion postulacion = Postulacion.builder()
                .idVacante(request.getIdVacante())
                .idUsuario(request.getIdUsuario())
                .estado(EstadoPostulacion.PENDIENTE)
                .fechaPostulacion(LocalDateTime.now())
                .build();

        Postulacion guardada = postulacionRepository.save(postulacion);
        log.info("Postulación {} creada", guardada.getId());
        return toResponse(guardada);
    }

    @Override
    @Transactional
    public PostulacionResponse actualizarEstado(Long id, EstadoPostulacionRequest request) {
        log.info("Actualizando estado de postulación {} a {}", id, request.getEstado());
        Postulacion postulacion = postulacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulación con id " + id + " no encontrada"));

        postulacion.setEstado(request.getEstado());
        Postulacion actualizada = postulacionRepository.save(postulacion);
        log.info("Postulación {} actualizada a estado {}", id, actualizada.getEstado());
        return toResponse(actualizada);
    }

    @Override
    public List<PostulacionResponse> historialPorUsuario(Long idUsuario) {
        log.info("Consultando historial de postulaciones del usuario {}", idUsuario);
        return postulacionRepository.findByIdUsuario(idUsuario).stream().map(this::toResponse).toList();
    }

    private PostulacionResponse toResponse(Postulacion postulacion) {
        return PostulacionResponse.builder()
                .id(postulacion.getId())
                .idVacante(postulacion.getIdVacante())
                .idUsuario(postulacion.getIdUsuario())
                .estado(postulacion.getEstado())
                .fechaPostulacion(postulacion.getFechaPostulacion())
                .build();
    }
}

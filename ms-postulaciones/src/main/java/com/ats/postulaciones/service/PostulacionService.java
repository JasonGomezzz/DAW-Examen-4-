package com.ats.postulaciones.service;

import com.ats.postulaciones.dto.EstadoPostulacionRequest;
import com.ats.postulaciones.dto.PostulacionRequest;
import com.ats.postulaciones.dto.PostulacionResponse;

import java.util.List;

public interface PostulacionService {
    PostulacionResponse crear(PostulacionRequest request);
    PostulacionResponse actualizarEstado(Long id, EstadoPostulacionRequest request);
    List<PostulacionResponse> historialPorUsuario(Long idUsuario);
}

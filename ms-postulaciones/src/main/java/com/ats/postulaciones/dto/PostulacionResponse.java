package com.ats.postulaciones.dto;

import com.ats.postulaciones.entity.EstadoPostulacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostulacionResponse {
    private Long id;
    private Long idVacante;
    private Long idUsuario;
    private EstadoPostulacion estado;
    private LocalDateTime fechaPostulacion;
}

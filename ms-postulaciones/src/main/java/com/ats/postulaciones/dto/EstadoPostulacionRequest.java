package com.ats.postulaciones.dto;

import com.ats.postulaciones.entity.EstadoPostulacion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstadoPostulacionRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoPostulacion estado;
}

package com.ats.postulaciones.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostulacionRequest {

    @NotNull(message = "El id de la vacante es obligatorio")
    private Long idVacante;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;
}

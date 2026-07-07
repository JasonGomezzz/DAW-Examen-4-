package com.ats.vacantes.dto;

import com.ats.vacantes.entity.EstadoVacante;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VacanteRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "Los requisitos son obligatorios")
    private String requisitos;

    @NotNull(message = "El salario es obligatorio")
    @Positive(message = "El salario debe ser mayor a 0")
    private Double salario;

    private EstadoVacante estado;
}

package com.ats.vacantes.dto;

import com.ats.vacantes.entity.EstadoVacante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacanteResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String requisitos;
    private Double salario;
    private EstadoVacante estado;
    private LocalDateTime fechaPublicacion;
}

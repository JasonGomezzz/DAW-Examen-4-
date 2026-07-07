package com.ats.postulaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacanteDto {
    private Long id;
    private String titulo;
    private String estado;
}

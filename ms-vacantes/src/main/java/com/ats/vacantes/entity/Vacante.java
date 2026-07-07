package com.ats.vacantes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vacantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descripcion;

    @Column(nullable = false, length = 2000)
    private String requisitos;

    @Column(nullable = false)
    private Double salario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVacante estado;

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;

    @PrePersist
    public void prePersist() {
        if (fechaPublicacion == null) {
            fechaPublicacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoVacante.ABIERTA;
        }
    }
}

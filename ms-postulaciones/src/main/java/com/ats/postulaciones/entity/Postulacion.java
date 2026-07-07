package com.ats.postulaciones.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "postulaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idVacante;

    @Column(nullable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPostulacion estado;

    @Column(nullable = false)
    private LocalDateTime fechaPostulacion;

    @PrePersist
    public void prePersist() {
        if (fechaPostulacion == null) {
            fechaPostulacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPostulacion.PENDIENTE;
        }
    }
}
